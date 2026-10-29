package com.indielens;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.indielens.apachepoi.CsvToXlsxConverterFinal;
import com.indielens.excelConverter.Reader;
import com.indielens.excelConverter.RegisterReader;
import com.indielens.jdbc.Conexao;
import com.indielens.logMachine.FileWritter;
import com.indielens.s3.config.S3Config;
import com.indielens.s3.service.S3Service;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.services.s3.S3Client;

public class Main {

    public static void main(String[] args) {
        FileWritter log = FileWritter.iniciarExecucao();
        int etapa = 1;
        boolean falhou = false;
        S3Client cliente = null;
        S3Service s3 = null;

        try {
            log.criarLog();
            log.CriarRegistro(1, "Checando configurações e conexão com o banco de dados", true);

            Dotenv env = Dotenv.load();
            validarConfiguracoes(env);
            cliente = S3Config.criarCliente();
            s3 = new S3Service(cliente, env.get("S3_BUCKET"));
            Conexao conexao = new Conexao(env);
            conexao.checarConexao();

            int ultimoRegistro = new RegisterReader(conexao).getRegistro();
            int idRegistro = ultimoRegistro + 1;
            log.definirRegistro(idRegistro);
            log.CriarRegistro(1, "Conexão com o banco validada. ID do registro: " + idRegistro, true);

            etapa = 2;
            log.CriarRegistro(2, "Iniciando busca e download do CSV no S3", true);
            String chaveCsv = s3.selecionarCsv(ultimoRegistro);
            Path pasta = Files.createTempDirectory("indielens-");
            Path csv = pasta.resolve("entrada.csv");
            Path xlsx = pasta.resolve("games-" + idRegistro + ".xlsx");
            log.CriarRegistro(2, "Arquivos temporários: " + pasta, true);
            try (InputStream entrada = s3.abrirArquivo(chaveCsv)) {
                Files.copy(entrada, csv);
            }
            log.CriarRegistro(2, "Download do CSV concluído: " + chaveCsv, true);
            CsvToXlsxConverterFinal.converter(csv.toString(), xlsx.toString(), log);

            etapa = 3;
            // O leitor chama a insercao a cada 100 jogos e ao terminar o lote restante.
            Reader leitor = new Reader();
            int totalInseridos = leitor.lerArquivoExcel(xlsx.toString(), idRegistro, log, conexao);

            etapa = 5;
            String chaveXlsx = "Processed/" + xlsx.getFileName();
            log.CriarRegistro(5, "Iniciando envio do XLSX ao S3: " + chaveXlsx, true);
            s3.enviarArquivo(xlsx, chaveXlsx);
            log.CriarRegistro(5, "Envio do XLSX concluído: " + chaveXlsx, true);
            Files.delete(xlsx);
            Files.delete(csv);
            Files.delete(pasta);
            log.CriarRegistro(5, "Aplicação concluída. Dados inseridos : " + totalInseridos, true);
        } catch (Exception e) {
            falhou = true;
            log.CriarRegistro(etapa, "Execução interrompida: " + e.getMessage(), false);
        } finally {
            if (s3 != null) {
                try {
                    Path arquivoLog = log.getCaminho();
                    String chaveLog = "Logs/" + arquivoLog.getFileName();
                    log.CriarRegistro(5, "Iniciando envio do log ao S3: " + chaveLog, true);
                    s3.enviarArquivo(arquivoLog, chaveLog);
                } catch (Exception e) {
                    falhou = true;
                    System.err.println("Falha ao enviar log ao S3: " + e.getMessage());
                }
            }
            if (cliente != null) {
                cliente.close();
            }
        }
        if (falhou) {
            throw new IllegalStateException("A execução não foi concluída; consulte o log.");
        }
    }

    private static void validarConfiguracoes(Dotenv env) {
        String[] obrigatorias = {"DB_HOST", "DB_PORT", "DB_NAME", "DB_USER", "DB_SENHA",
            "S3_BUCKET"};
        for (String chave : obrigatorias) {
            String valor = env.get(chave);
            if (valor == null || valor.trim().isEmpty()) {
                throw new IllegalArgumentException("Configuração obrigatória ausente: " + chave);
            }
        }
    }
}
