package com.indielens.excelConverter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.github.pjfanning.xlsx.SharedStringsImplementationType;
import com.github.pjfanning.xlsx.StreamingReader;
import com.indielens.jdbc.methods.JogosExtractor;
import com.indielens.jdbc.Conexao;
import com.indielens.jdbc.tables.Jogo;
import com.indielens.jdbc.tables.Jogo_auxiliar;
import com.indielens.jdbc.tables.Jogo_detalhes;
import com.indielens.jdbc.tables.Jogo_main;
import com.indielens.jdbc.tables.Jogo_metricas;
import com.indielens.jdbc.tables.Jogo_midia;
import com.indielens.jdbc.tables.Jogo_playtime;
import com.indielens.jdbc.tables.Jogo_suporte;
import com.indielens.logMachine.FileWritter;

import io.github.cdimascio.dotenv.Dotenv;

public class Reader {
	
	public void lerArquivoExcel(String caminhoDoArquivo) {
        String caminho = caminhoDoArquivo == null || caminhoDoArquivo.trim().isEmpty()
            ? Dotenv.load().get("XLSX_PATH") : caminhoDoArquivo;
        Conexao conexao = new Conexao();
        int idRegistro = new RegisterReader(conexao).getRegistro() + 1;
        FileWritter log = new FileWritter(idRegistro);
        try {
            lerArquivoExcel(caminho, idRegistro, log, conexao);
        } catch (IOException | RuntimeException e) {
            log.CriarRegistro(3, "Importação interrompida: " + e.getMessage(), false);
            throw new IllegalStateException("Falha na importação do XLSX", e);
        }
    }

    public int lerArquivoExcel(String caminho, int idRegistro, FileWritter log,
            Conexao conexao) throws IOException {
        List<Jogo> listaJogos = new ArrayList<>();
        int totalInseridos = 0;
        int linhaAtual = 0;
        log.CriarRegistro(3, "Iniciando leitura do XLSX. ID do registro: " + idRegistro, true);
        try (
            // Mantem somente 100 linhas em memoria e usa disco para os textos compartilhados.
            Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .setSharedStringsImplementationType(SharedStringsImplementationType.TEMP_FILE_BACKED)
                .open(new File(caminho))
        ) {
            Sheet aba = workbook.getSheetAt(0);

            int id_registro = idRegistro;
            
            for (Row linha : aba) {
            	
                if (linha.getRowNum() == 0) {
                    continue;
                }
                linhaAtual = linha.getRowNum() + 1;

                // O conversor CSV -> XLSX grava todas as células como texto.
                Integer id_jogo = lerInteiro(linha, 0);
                Integer peak_CCU = lerInteiro(linha, 4);
                Integer idade_minima = lerInteiro(linha, 5);
                Integer desconto_pct = lerInteiro(linha, 7);
                Integer qtd_dlc = lerInteiro(linha, 8);
                Integer nota_metacritic = lerInteiro(linha, 20);
                Double nota_usuarios = lerDecimal(linha, 22);
                Integer avaliacoes_positivas = lerInteiro(linha, 23);
                Integer avaliacoes_negativas = lerInteiro(linha, 24);
                Integer nota_score = lerInteiro(linha, 25);
                Integer qtd_conquistas = lerInteiro(linha, 26);
                Integer qtd_recomendacoes = lerInteiro(linha, 27);
                Integer media_horas_total = lerInteiro(linha, 29);
                Integer media_horas_duas_semanas = lerInteiro(linha, 30);
                Integer mediana_horas_total = lerInteiro(linha, 31);
                Integer mediana_horas_duas_semanas = lerInteiro(linha, 32);
                
                String nome = lerTexto(linha, 1);
                String descricao = lerTexto(linha, 9);
                String idiomas_suportados = lerTexto(linha, 10);
                String idiomas_suportados_audio = lerTexto(linha, 11);
                String usuario_review = lerTexto(linha, 12);
                String imagem_cabecalho = lerTexto(linha, 13);
                String site_oficial = lerTexto(linha, 14);
                String url_suporte = lerTexto(linha, 15);
                String email_suporte = lerTexto(linha, 16);
                Boolean suporte_windows = lerBooleano(linha, 17);
                Boolean suporte_MAC = lerBooleano(linha, 18);
                Boolean suporte_linux = lerBooleano(linha, 19);
                String url_metacritic = lerTexto(linha, 21);
                String jogo_anotacoes = lerTexto(linha, 28);
                String desenvolvedor = lerTexto(linha, 33);
                String publicante = lerTexto(linha, 34);
                String categorias = lerTexto(linha, 35);
                String generos = lerTexto(linha, 36);
                String tags = lerTexto(linha, 37);
                String capturas_tela_URL = lerTexto(linha, 38);
                String videos_URL = lerTexto(linha, 39);
                
                //fazendo a média dos proprietários, sendo assim removendo o "min - max" que mostra no site
                String proprietariosEstimados = lerTexto(linha, 3);
                String[] valores = proprietariosEstimados.split("-");
                Integer proprietarios_min = Integer.parseInt(valores[0].trim());
                Integer proprietarios_max = Integer.parseInt(valores[1].trim());
                Integer proprietarios_estimados = proprietarios_min + (proprietarios_max - proprietarios_min) / 2;
                
                //formatando a data só pelo amor ao esporte, dava pra salvar como string
                String dataLancamento = lerTexto(linha, 2).trim();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
                LocalDate data_lancamento = LocalDate.parse(dataLancamento, formatter);
                
                Double preco = lerDecimal(linha, 6);

                // criando objetos 
                Jogo_main jogoAtualMain = new Jogo_main("default", id_jogo, id_registro, nome, desenvolvedor, idade_minima, proprietarios_estimados, preco, data_lancamento);
                Jogo_auxiliar jogoAtualAuxiliar = new Jogo_auxiliar(id_jogo, id_registro, desconto_pct, site_oficial, email_suporte, url_metacritic, url_suporte, usuario_review);
                Jogo_detalhes jogoAtualDetalhes = new Jogo_detalhes(id_jogo, id_registro, jogo_anotacoes, publicante, descricao, tags, generos, categorias);
                Jogo_metricas jogoAtualMetricas = new Jogo_metricas(id_jogo, id_registro, peak_CCU, avaliacoes_positivas, avaliacoes_negativas, qtd_dlc, qtd_recomendacoes, qtd_conquistas, nota_metacritic, nota_usuarios, nota_score);
                Jogo_suporte jogoAtualSuporte = new Jogo_suporte(id_jogo, id_registro, suporte_windows, suporte_MAC, suporte_linux, idiomas_suportados, idiomas_suportados_audio);
                Jogo_midia jogoAtualMidia = new Jogo_midia(id_jogo, id_registro, imagem_cabecalho, capturas_tela_URL, videos_URL);
                Jogo_playtime jogoAtualPlaytime = new Jogo_playtime(id_jogo, id_registro, media_horas_total, media_horas_duas_semanas, mediana_horas_total, mediana_horas_duas_semanas);

                // centralizando Objetos nem um unico objeto para facilitar commits
                Jogo jogoAtual = new Jogo(jogoAtualMain, jogoAtualMidia, jogoAtualSuporte,jogoAtualDetalhes, jogoAtualMetricas, jogoAtualPlaytime, jogoAtualAuxiliar);
                
                //adicionando na lista
                listaJogos.add(jogoAtual);
                
                if (listaJogos.size() == 1000) {
                    log.CriarRegistro(3, "lista de jogos completa, iniciando insersão no banco", true);
                    totalInseridos = inserirBatch(listaJogos, totalInseridos, log, conexao);
                }
            }

            // Grava os jogos restantes quando o arquivo termina antes de ter 100 ultimos fechados.
            if (!listaJogos.isEmpty()) {
                totalInseridos = inserirBatch(listaJogos, totalInseridos, log, conexao);
            }

        } catch (IOException | RuntimeException e) {
            log.CriarRegistro(3, "Leitura/importação interrompida na linha " + linhaAtual
                + "; dados inseridos : " + totalInseridos + ". " + e.getMessage(), false);
            throw e;
        }
        log.CriarRegistro(3, "Leitura do XLSX concluída. Dados inseridos : " + totalInseridos, true);
        return totalInseridos;
    }

    private static int inserirBatch(List<Jogo> listaJogos, int totalInseridos, FileWritter log,
            Conexao conexao) {
        int tamanhoBatch = listaJogos.size();
        log.CriarRegistro(4, "Inserindo batch de " + tamanhoBatch
            + ", dados inseridos : " + totalInseridos, true);
        if (!JogosExtractor.InserirBanco(listaJogos, conexao, log)) {
            log.CriarRegistro(4, "Falha na inserção do batch de " + tamanhoBatch
                + ", dados inseridos : " + totalInseridos, false);
            throw new IllegalStateException("O batch não foi salvo; os batches anteriores permanecem confirmados.");
        }
        totalInseridos += tamanhoBatch;
        listaJogos.clear();
        log.CriarRegistro(4, "Commit concluído. Batch de " + tamanhoBatch
            + ", dados inseridos : " + totalInseridos, true);
        return totalInseridos;
    }

    private static String lerTexto(Row linha, int coluna) {
    	//faz o getcell, caso esteja vazio retorna "null" ← facilitará quando for pro jdbc
        Cell celula = linha.getCell(coluna, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return celula == null ? "" : celula.getStringCellValue();
    }

    //sub método para INT
    private static Integer lerInteiro(Row linha, int coluna) {
        String valor = lerTexto(linha, coluna).trim();
        return valor.isEmpty() ? null : Integer.valueOf(valor.replaceFirst("\\.0+$", ""));
    }

    //sub método para double
    private static Double lerDecimal(Row linha, int coluna) {
        String valor = lerTexto(linha, coluna).trim();
        return valor.isEmpty() ? null : Double.valueOf(valor);
    }

	  //sub método para Booleano
    //see aqui convertemos o tinyINT
    private static Boolean lerBooleano(Row linha, int coluna) {
        String valor = lerTexto(linha, coluna).trim();
        if (valor.isEmpty()) {
            return null;
        }
        if ("true".equalsIgnoreCase(valor) || "1".equals(valor)) {
            return true;
        }
        if ("false".equalsIgnoreCase(valor) || "0".equals(valor)) {
            return false;
        }
        throw new IllegalArgumentException("Booleano inválido na linha "
            + (linha.getRowNum() + 1) + ", coluna " + (coluna + 1) + ": " + valor);
    }

}
