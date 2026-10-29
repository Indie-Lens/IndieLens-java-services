package com.indielens.excelConverter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.Styles;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.github.pjfanning.poi.xssf.streaming.TempFileSharedStringsTable;

/** Leitura sequencial da primeira aba, sem materializar a planilha inteira. */
final class SaxXlsxReader {
    private static final int COLUNAS = 40;
    // Preserva a janela de leitura anterior antes de entregar linhas para a importação.
    private static final int LINHAS_POR_LEITURA = 100;

    @FunctionalInterface
    interface ConsumidorLinha {
        void aceitar(int numeroLinha, String[] valores);
    }

    private SaxXlsxReader() {
    }

    static void ler(File arquivo, ConsumidorLinha consumidor) throws IOException {
        try (OPCPackage pacote = OPCPackage.open(arquivo, PackageAccess.READ);
             // Mantém a mesma estratégia de strings compartilhadas em disco do leitor anterior.
             TempFileSharedStringsTable strings = new TempFileSharedStringsTable(pacote, false, false)) {
            XSSFReader leitor = new XSSFReader(pacote);
            Iterator<InputStream> abas = leitor.getSheetsData();
            if (!abas.hasNext()) {
                throw new IOException("O XLSX não possui uma primeira aba.");
            }

            ProcessadorLinhas linhas = new ProcessadorLinhas(consumidor, strings);
            XMLReader xml = XMLHelper.newXMLReader();
            xml.setContentHandler(new ProcessadorCelulas(leitor.getStylesTable(), strings, linhas,
                usaDatas1904(leitor)));
            try (InputStream aba = abas.next()) {
                xml.parse(new InputSource(aba));
                linhas.entregarPendentes();
            }
        } catch (OpenXML4JException | ParserConfigurationException | SAXException e) {
            throw new IOException("Falha ao ler o XLSX", e);
        }
    }

    private static boolean usaDatas1904(XSSFReader leitor)
            throws IOException, SAXException, ParserConfigurationException, OpenXML4JException {
        boolean[] usa1904 = {false};
        XMLReader xml = XMLHelper.newXMLReader();
        xml.setContentHandler(new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes atributos) {
                if ("workbookPr".equals(localName)) {
                    String valor = atributos.getValue("date1904");
                    usa1904[0] = "1".equals(valor) || "true".equalsIgnoreCase(valor);
                }
            }
        });
        try (InputStream propriedades = leitor.getWorkbookData()) {
            xml.parse(new InputSource(propriedades));
        }
        return usa1904[0];
    }

    private static final class ProcessadorLinhas implements XSSFSheetXMLHandler.SheetContentsHandler {
        private final ConsumidorLinha consumidor;
        private final TempFileSharedStringsTable strings;
        private final StringBuilder valorBruto = new StringBuilder();
        private final List<Linha> pendentes = new ArrayList<>(LINHAS_POR_LEITURA);
        private String[] valores;
        private String tipoCelula;
        private int ultimaColuna;
        private boolean lendoValorBruto;
        private boolean emTextoInline;
        private boolean dataInvalida;

        private ProcessadorLinhas(ConsumidorLinha consumidor, TempFileSharedStringsTable strings) {
            this.consumidor = consumidor;
            this.strings = strings;
        }

        @Override
        public void startRow(int numeroLinha) {
            valores = new String[COLUNAS];
            Arrays.fill(valores, "");
            ultimaColuna = -1;
        }

        @Override
        public void endRow(int numeroLinha) {
            pendentes.add(new Linha(numeroLinha, valores));
            if (pendentes.size() == LINHAS_POR_LEITURA) {
                entregarPendentes();
            }
        }

        private void entregarPendentes() {
            for (Linha linha : pendentes) {
                consumidor.aceitar(linha.numero(), linha.valores());
            }
            pendentes.clear();
        }

        @Override
        public void cell(String referencia, String valor, XSSFComment comentario) {
            int coluna = referencia == null ? ultimaColuna + 1 : indiceColuna(referencia);
            ultimaColuna = coluna;
            if (coluna < 0 || coluna >= valores.length) {
                return;
            }
            // StreamingReader retorna o valor bruto de células booleanas e de erro.
            if ("b".equals(tipoCelula)) {
                valores[coluna] = valorBruto.toString();
            } else if ("e".equals(tipoCelula)) {
                valores[coluna] = "ERROR:  " + valorBruto;
            } else if ("s".equals(tipoCelula) && valorBruto.length() > 0) {
                valores[coluna] = strings.getString(Integer.parseInt(valorBruto.toString()));
            } else if ("inlineStr".equals(tipoCelula)) {
                valores[coluna] = valorBruto.toString();
            } else if ("d".equals(tipoCelula) && dataInvalida) {
                valores[coluna] = valorBruto.toString();
            } else {
                valores[coluna] = valor == null ? "" : valor;
            }
        }

        private static int indiceColuna(String referencia) {
            int coluna = 0;
            int indice = 0;
            while (indice < referencia.length()) {
                char caractere = referencia.charAt(indice++);
                if (caractere < 'A' || caractere > 'Z') {
                    break;
                }
                coluna = coluna * 26 + caractere - 'A' + 1;
            }
            return coluna - 1;
        }
    }

    private record Linha(int numero, String[] valores) {
    }

    private static final class ProcessadorCelulas extends XSSFSheetXMLHandler {
        private final ProcessadorLinhas linhas;
        private final boolean usa1904;

        private ProcessadorCelulas(Styles estilos, SharedStrings strings, ProcessadorLinhas linhas,
                boolean usa1904) {
            super(estilos, strings, linhas, new DataFormatter(), false);
            this.linhas = linhas;
            this.usa1904 = usa1904;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atributos)
                throws SAXException {
            if ("c".equals(localName)) {
                linhas.tipoCelula = atributos.getValue("t");
                linhas.valorBruto.setLength(0);
                linhas.dataInvalida = false;
            } else if ("v".equals(localName)
                    && ("b".equals(linhas.tipoCelula) || "e".equals(linhas.tipoCelula)
                        || "s".equals(linhas.tipoCelula) || "d".equals(linhas.tipoCelula))) {
                linhas.lendoValorBruto = true;
            } else if ("is".equals(localName) && "inlineStr".equals(linhas.tipoCelula)) {
                linhas.emTextoInline = true;
            } else if ("t".equals(localName) && linhas.emTextoInline) {
                linhas.lendoValorBruto = true;
            }
            super.startElement(uri, localName, qName, atributos);
        }

        @Override
        public void characters(char[] caracteres, int inicio, int tamanho) throws SAXException {
            if (linhas.lendoValorBruto) {
                linhas.valorBruto.append(caracteres, inicio, tamanho);
                if ("d".equals(linhas.tipoCelula)) {
                    return;
                }
            }
            super.characters(caracteres, inicio, tamanho);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("v".equals(localName) && "d".equals(linhas.tipoCelula)) {
                String serial;
                try {
                    serial = Double.toString(converterDataIso(linhas.valorBruto.toString(), usa1904));
                } catch (RuntimeException e) {
                    linhas.dataInvalida = true;
                    serial = "0";
                }
                char[] caracteres = serial.toCharArray();
                super.characters(caracteres, 0, caracteres.length);
            }
            super.endElement(uri, localName, qName);
            if ("v".equals(localName) || "t".equals(localName)) {
                linhas.lendoValorBruto = false;
            } else if ("is".equals(localName)) {
                linhas.emTextoInline = false;
            }
        }

        private static double converterDataIso(String valor, boolean usa1904) {
            try {
                return DateUtil.getExcelDate(LocalDateTime.parse(valor), usa1904);
            } catch (DateTimeParseException e) {
                try {
                    return DateUtil.getExcelDate(LocalDate.parse(valor).atStartOfDay(), usa1904);
                } catch (DateTimeParseException dataInvalida) {
                    int fracao = valor.lastIndexOf('.');
                    return DateUtil.convertTime(fracao < 0 ? valor : valor.substring(0, fracao));
                }
            }
        }
    }
}
