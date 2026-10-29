package com.indielens.excelConverter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.pjfanning.xlsx.SharedStringsImplementationType;
import com.github.pjfanning.xlsx.StreamingReader;

class SaxXlsxReaderTest {
    @TempDir
    Path diretorio;

    @Test
    void preservaValoresDeStringsCompartilhadasTiposNativosEPrimeiraAba() throws Exception {
        Path arquivo = diretorio.resolve("compartilhadas.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Row cabecalho = workbook.createSheet("Dados").createRow(0);
            cabecalho.createCell(0).setCellValue("AppID");
            Row linha = workbook.getSheetAt(0).createRow(3);
            linha.createCell(0).setCellValue("0001.00");
            linha.createCell(1).setCellValue(true);
            linha.createCell(3).setCellValue(false);
            linha.createCell(5).setCellValue(12.5);
            linha.createCell(7).setCellErrorValue(FormulaError.DIV0.getCode());
            XSSFRichTextString descricao = new XSSFRichTextString("  Café 🎮\nlinha '2'  ");
            descricao.applyFont(2, 6, workbook.createFont());
            linha.createCell(9).setCellValue(descricao);
            linha.createCell(10).setCellValue("literais _x000D_ e _x005F_x000D_");
            Cell formula = linha.createCell(28);
            formula.setCellFormula("1+2");
            workbook.getCreationHelper().createFormulaEvaluator().evaluateFormulaCell(formula);
            linha.createCell(39).setCellValue("última coluna");
            linha.createCell(40).setCellValue("fora das colunas importadas");
            workbook.createSheet("Ignorada").createRow(0).createCell(0).setCellValue("outra aba");
            try (OutputStream saida = Files.newOutputStream(arquivo)) {
                workbook.write(saida);
            }
        }
        compararComLeitorAnterior(arquivo);
    }

    @Test
    void preservaStringsInlineECelulasEsparsas() throws Exception {
        Path arquivo = diretorio.resolve("inline.xlsx");
        SXSSFWorkbook workbook = new SXSSFWorkbook(10);
        try {
            Row linha = workbook.createSheet("Dados").createRow(4);
            linha.createCell(0).setCellValue("42");
            linha.createCell(9).setCellValue("aspas \" e linha\nseguinte 🎮");
            linha.createCell(10).setCellValue("literal _x000D_ e _x005F_");
            linha.createCell(39).setCellValue("fim");
            try (OutputStream saida = Files.newOutputStream(arquivo)) {
                workbook.write(saida);
            }
        } finally {
            workbook.close();
            workbook.dispose();
        }
        compararComLeitorAnterior(arquivo);
        Path rico = diretorio.resolve("inline-rico.xlsx");
        adicionarTextoRicoInline(arquivo, rico);
        compararComLeitorAnterior(rico);
    }

    @Test
    void fechaOArquivoMesmoQuandoOConsumidorDaLinhaFalha() throws Exception {
        Path arquivo = diretorio.resolve("falha.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            workbook.createSheet("Dados").createRow(1).createCell(0).setCellValue("42");
            try (OutputStream saida = Files.newOutputStream(arquivo)) {
                workbook.write(saida);
            }
        }
        assertThrows(IllegalStateException.class, () -> SaxXlsxReader.ler(arquivo.toFile(), (linha, valores) -> {
            throw new IllegalStateException("falha simulada");
        }));
        assertTrue(Files.deleteIfExists(arquivo));
    }

    @Test
    void preservaDatasIsoComOsSistemas1900E1904() throws Exception {
        for (boolean usa1904 : new boolean[]{false, true}) {
            Path original = diretorio.resolve("data-original-" + usa1904 + ".xlsx");
            Path arquivo = diretorio.resolve("data-iso-" + usa1904 + ".xlsx");
            int formatoData;
            int formatoHora;
            try (XSSFWorkbook workbook = new XSSFWorkbook()) {
                if (!workbook.getCTWorkbook().isSetWorkbookPr()) {
                    workbook.getCTWorkbook().addNewWorkbookPr();
                }
                workbook.getCTWorkbook().getWorkbookPr().setDate1904(usa1904);
                var estiloData = workbook.createCellStyle();
                estiloData.setDataFormat(workbook.createDataFormat().getFormat("MMM d, yyyy"));
                var estiloHora = workbook.createCellStyle();
                estiloHora.setDataFormat(workbook.createDataFormat().getFormat("hh:mm:ss"));
                formatoData = estiloData.getIndex();
                formatoHora = estiloHora.getIndex();
                Row linha = workbook.createSheet("Dados").createRow(0);
                for (int i = 0; i < 5; i++) linha.createCell(i).setCellValue(1.0);
                try (OutputStream saida = Files.newOutputStream(original)) {
                    workbook.write(saida);
                }
            }
            String[] datas = {"2020-01-02T00:00:00", "2020-01-02", "12:34:56",
                "12:34:56.123", "data-invalida"};
            int[] estilos = {formatoData, 0, formatoHora, formatoHora, formatoData};
            reescreverPrimeiraAba(original, arquivo, xml -> {
                for (int i = 0; i < datas.length; i++) {
                    String referencia = Character.toString('A' + i) + "1";
                    Matcher celula = Pattern.compile("<c r=\"" + referencia + "\"[^>]*>.*?</c>").matcher(xml);
                    assertTrue(celula.find(), "a fixture deve conter a célula " + referencia);
                    String substituicao = "<c r=\"" + referencia + "\" t=\"d\" s=\"" + estilos[i]
                        + "\"><v>" + datas[i] + "</v></c>";
                    xml = celula.replaceFirst(Matcher.quoteReplacement(substituicao));
                }
                return xml;
            });
            compararComLeitorAnterior(arquivo);
        }
    }

    private static void compararComLeitorAnterior(Path arquivo) throws Exception {
        List<Integer> indicesEsperados = new ArrayList<>();
        List<String[]> valoresEsperados = new ArrayList<>();
        try (Workbook workbook = StreamingReader.builder().rowCacheSize(100)
                .setSharedStringsImplementationType(SharedStringsImplementationType.TEMP_FILE_BACKED)
                .open(arquivo.toFile())) {
            for (Row linha : workbook.getSheetAt(0)) {
                String[] valores = new String[40];
                for (int coluna = 0; coluna < valores.length; coluna++) {
                    Cell celula = linha.getCell(coluna, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    valores[coluna] = celula == null ? "" : celula.getStringCellValue();
                }
                indicesEsperados.add(linha.getRowNum());
                valoresEsperados.add(valores);
            }
        }

        List<Integer> indicesObtidos = new ArrayList<>();
        List<String[]> valoresObtidos = new ArrayList<>();
        SaxXlsxReader.ler(arquivo.toFile(), (indice, valores) -> {
            indicesObtidos.add(indice);
            valoresObtidos.add(valores);
        });

        assertEquals(indicesEsperados, indicesObtidos);
        assertEquals(valoresEsperados.size(), valoresObtidos.size());
        for (int i = 0; i < valoresEsperados.size(); i++) {
            assertArrayEquals(valoresEsperados.get(i), valoresObtidos.get(i), "linha " + indicesEsperados.get(i));
        }
    }

    private static void adicionarTextoRicoInline(Path origem, Path destino) throws Exception {
        reescreverPrimeiraAba(origem, destino, xml -> {
            Matcher celula = Pattern.compile("<c r=\"J5\"[^>]*>.*?</c>").matcher(xml);
            assertTrue(celula.find(), "a fixture deve conter a célula J5");
            String textoRico = "<c r=\"J5\" t=\"inlineStr\"><is>"
                + "<r><t>texto _x000D_</t></r><rPh sb=\"0\" eb=\"4\"><t>fonético</t></rPh>"
                + "</is></c>";
            return celula.replaceFirst(Matcher.quoteReplacement(textoRico));
        });
    }

    private static void reescreverPrimeiraAba(Path origem, Path destino, UnaryOperator<String> alterar)
            throws Exception {
        boolean alterado = false;
        try (ZipFile entrada = new ZipFile(origem.toFile());
             ZipOutputStream saida = new ZipOutputStream(Files.newOutputStream(destino))) {
            var entradas = entrada.entries();
            while (entradas.hasMoreElements()) {
                ZipEntry item = entradas.nextElement();
                saida.putNextEntry(new ZipEntry(item.getName()));
                try (var conteudo = entrada.getInputStream(item)) {
                    if ("xl/worksheets/sheet1.xml".equals(item.getName())) {
                        String xml = new String(conteudo.readAllBytes(), StandardCharsets.UTF_8);
                        saida.write(alterar.apply(xml).getBytes(StandardCharsets.UTF_8));
                        alterado = true;
                    } else {
                        conteudo.transferTo(saida);
                    }
                }
                saida.closeEntry();
            }
        }
        assertTrue(alterado, "a fixture deve conter o XML da primeira aba");
    }
}
