package com.indielens;

import com.indielens.apachepoi.CsvToXlsxConverterFinal;
import com.indielens.excelConverter.Reader;
import com.indielens.excelConverter.RegisterReader;
import com.indielens.jdbc.Conexao;
import com.indielens.logMachine.FileWritter;

import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    public static void main(String[] args) {
        FileWritter log = FileWritter.iniciarExecucao();
        int etapa = 1;

        try {
            log.criarLog();
            log.CriarRegistro(1, "Checando configurações e conexão com o banco de dados", true);

            Dotenv env = Dotenv.load();
            validarConfiguracoes(env);
            Conexao conexao = new Conexao(env);
            conexao.checarConexao();

            int idRegistro = new RegisterReader(conexao).getRegistro() + 1;
            log.definirRegistro(idRegistro);
            log.CriarRegistro(1, "Conexão com o banco validada. ID do registro: " + idRegistro, true);

            etapa = 2;
            CsvToXlsxConverterFinal.converter(env.get("CSV_PATH"), env.get("XLSX_PATH"), log);

            etapa = 3;
            // O leitor confirma lotes de até 1000 jogos e o lote restante.
            Reader leitor = new Reader();
            int totalInseridos = leitor.lerArquivoExcel(env.get("XLSX_PATH"), idRegistro, log, conexao);

            etapa = 5;
            log.CriarRegistro(5, "Aplicação concluída. Dados inseridos : " + totalInseridos, true);
        } catch (Exception e) {
            log.CriarRegistro(etapa, "Execução interrompida: " + e.getMessage(), false);
        }
    }

    private static void validarConfiguracoes(Dotenv env) {
        String[] obrigatorias = {"DB_HOST", "DB_PORT", "DB_NAME", "DB_USER", "DB_SENHA",
            "CSV_PATH", "XLSX_PATH"};
        for (String chave : obrigatorias) {
            String valor = env.get(chave);
            if (valor == null || valor.trim().isEmpty()) {
                throw new IllegalArgumentException("Configuração obrigatória ausente: " + chave);
            }
        }
    }
}
