package com.indielens.excelConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.indielens.jdbc.Conexao;
import com.indielens.logMachine.FileWritter;

class ReaderSaxIntegrationTest {
    private static final String[] TABELAS = {"jogo_main", "jogo_midia", "jogo_suporte", "jogo_detalhes",
        "jogo_metricas", "jogo_playtime", "jogo_auxiliar"};

    @TempDir
    Path diretorio;

    @Test
    void importaLoteCompletoERestanteNasSeteTabelas() throws Exception {
        try (Banco banco = new Banco(false)) {
            int inseridos = importar(banco, criarXlsx(false));
            assertEquals(1001, inseridos);
            banco.conferirContagens(1001);
            try (Statement comando = banco.observador.createStatement();
                 ResultSet resultado = comando.executeQuery(
                         "SELECT descricao FROM jogo_detalhes WHERE id_jogo = 1001 AND id_registro = 1")) {
                resultado.next();
                assertEquals("texto _x000D_\ncom acento é", resultado.getString(1));
            }
            try (Statement comando = banco.observador.createStatement();
                 ResultSet resultado = comando.executeQuery(
                         "SELECT peak_CCU FROM jogo_metricas WHERE id_jogo = 1001 AND id_registro = 1")) {
                resultado.next();
                assertEquals(17, resultado.getInt(1));
            }
        }
    }

    @Test
    void erroNaTabelaFilhaDesfazUltimoLoteEPreservaAnterior() throws Exception {
        try (Banco banco = new Banco(true)) {
            Path arquivo = criarXlsx(false);
            assertThrows(IllegalStateException.class, () -> importar(banco, arquivo));
            banco.conferirContagens(1000);
        }
    }

    @Test
    void erroDeLeituraNaoDesfazLoteAnterior() throws Exception {
        try (Banco banco = new Banco(false)) {
            Path arquivo = criarXlsx(true);
            assertThrows(RuntimeException.class, () -> importar(banco, arquivo));
            banco.conferirContagens(1000);
        }
    }

    @Test
    void xmlCorrompidoNaoConfirmaLinhasDaUltimaJanelaIncompleta() throws Exception {
        try (Banco banco = new Banco(false)) {
            Path arquivo = diretorio.resolve("corrompido.xlsx");
            truncarXmlDaPlanilha(criarXlsx(false), arquivo);
            assertThrows(Exception.class, () -> importar(banco, arquivo));
            banco.conferirContagens(0);
        }
    }

    private int importar(Banco banco, Path arquivo) throws Exception {
        FileWritter log = new FileWritter(diretorio.resolve("importacao.log"));
        return new Reader().lerArquivoExcel(arquivo.toString(), 1, log, new Conexao(banco.fonte));
    }

    private Path criarXlsx(boolean dataInvalidaNoUltimoJogo) throws Exception {
        Path arquivo = diretorio.resolve("jogos.xlsx");
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        try {
            var aba = workbook.createSheet("Dados");
            aba.createRow(0).createCell(0).setCellValue("AppID");
            for (int id = 1; id <= 1001; id++) {
                Row linha = aba.createRow(id);
                linha.createCell(0).setCellValue(id == 1001 ? "1001.000" : Integer.toString(id));
                linha.createCell(4).setCellValue(id == 1001 ? "17.00" : "0");
                linha.createCell(1).setCellValue("Jogo " + id);
                linha.createCell(2).setCellValue(dataInvalidaNoUltimoJogo && id == 1001
                    ? "data inválida" : "Jan 1, 2020");
                linha.createCell(3).setCellValue("0 - 100");
                linha.createCell(9).setCellValue("texto _x000D_\ncom acento é");
            }
            try (OutputStream saida = Files.newOutputStream(arquivo)) {
                workbook.write(saida);
            }
        } finally {
            workbook.close();
            workbook.dispose();
        }
        return arquivo;
    }

    private static void truncarXmlDaPlanilha(Path origem, Path destino) throws Exception {
        try (ZipFile entrada = new ZipFile(origem.toFile());
             ZipOutputStream saida = new ZipOutputStream(Files.newOutputStream(destino))) {
            var entradas = entrada.entries();
            while (entradas.hasMoreElements()) {
                ZipEntry item = entradas.nextElement();
                saida.putNextEntry(new ZipEntry(item.getName()));
                try (var conteudo = entrada.getInputStream(item)) {
                    if ("xl/worksheets/sheet1.xml".equals(item.getName())) {
                        String xml = new String(conteudo.readAllBytes(), StandardCharsets.UTF_8);
                        int fim = xml.lastIndexOf("</worksheet>");
                        if (fim < 0) throw new IllegalStateException("Planilha sintética sem fechamento");
                        saida.write(xml.substring(0, fim).getBytes(StandardCharsets.UTF_8));
                    } else {
                        conteudo.transferTo(saida);
                    }
                }
                saida.closeEntry();
            }
        }
    }

    private static final class Banco implements AutoCloseable {
        private final DriverManagerDataSource fonte;
        private final Connection observador;

        private Banco(boolean rejeitarUltimoJogo) throws Exception {
            String url = "jdbc:h2:mem:sax_" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1";
            fonte = new DriverManagerDataSource(url, "sa", "");
            observador = fonte.getConnection();
            try (Statement sql = observador.createStatement()) {
                sql.execute("CREATE TABLE registro (id_registro INT PRIMARY KEY)");
                sql.execute("CREATE TABLE jogo_main (id_jogo_rastreavel INT AUTO_INCREMENT PRIMARY KEY, "
                    + "id_jogo INT NOT NULL, id_registro INT NOT NULL, nome VARCHAR(255), desenvolvedor VARCHAR(100), "
                    + "idade_minima INT, proprietarios_estimados INT, preco DECIMAL(8,2), data_lancamento DATE, "
                    + "UNIQUE (id_jogo,id_registro), FOREIGN KEY (id_registro) REFERENCES registro(id_registro))");
                String chave = ", PRIMARY KEY (id_jogo,id_registro), FOREIGN KEY (id_jogo,id_registro) "
                    + "REFERENCES jogo_main(id_jogo,id_registro))";
                sql.execute("CREATE TABLE jogo_midia (id_jogo INT, id_registro INT, imagem_cabecalho CLOB, "
                    + "capturas_tela_URL CLOB, videos_URL CLOB" + chave);
                sql.execute("CREATE TABLE jogo_suporte (id_jogo INT, id_registro INT, suporte_windows BOOLEAN, "
                    + "suporte_MAC BOOLEAN, suporte_linux BOOLEAN, idiomas_suportados CLOB, idiomas_suportados_audio CLOB" + chave);
                sql.execute("CREATE TABLE jogo_detalhes (id_jogo INT, id_registro INT, jogo_anotacoes CLOB, "
                    + "publicante CLOB, descricao CLOB, tags CLOB, generos CLOB, categorias CLOB" + chave);
                sql.execute("CREATE TABLE jogo_metricas (id_jogo INT, id_registro INT, peak_CCU INT, "
                    + "avaliacoes_positivas INT, avaliacoes_negativas INT, qtd_dlc INT, qtd_recomendacoes INT, "
                    + "qtd_conquistas INT, nota_metacritic INT, nota_usuarios DECIMAL(5,2), nota_score INT" + chave);
                sql.execute("CREATE TABLE jogo_playtime (id_jogo INT, id_registro INT, media_horas_total INT, "
                    + "media_horas_duas_semanas INT, mediana_horas_total INT, mediana_horas_duas_semanas INT" + chave);
                sql.execute("CREATE TABLE jogo_auxiliar (id_jogo INT, id_registro INT, desconto_pct INT, "
                    + "site_oficial CLOB, email_suporte CLOB, url_metacritic CLOB, url_suporte CLOB, usuario_review CLOB"
                    + (rejeitarUltimoJogo ? ", CHECK (id_jogo <> 1001)" : "") + chave);
            }
        }

        private void conferirContagens(int esperado) throws Exception {
            try (Statement sql = observador.createStatement()) {
                for (String tabela : TABELAS) {
                    try (ResultSet resultado = sql.executeQuery("SELECT COUNT(*) FROM " + tabela)) {
                        resultado.next();
                        assertEquals(esperado, resultado.getInt(1), tabela);
                    }
                }
            }
        }

        @Override
        public void close() throws Exception {
            try (Statement sql = observador.createStatement()) {
                sql.execute("SHUTDOWN");
            } finally {
                observador.close();
            }
        }
    }
}
