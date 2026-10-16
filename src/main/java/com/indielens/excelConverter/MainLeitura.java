package com.indielens.excelConverter;

public class MainLeitura {

    public static void main(String[] args) {
        Reader leitor = new Reader();
        System.out.println("Iniciando leitura do XLSX e importacao para o banco de dados.");
        leitor.lerArquivoExcel(args.length == 0 ? null : args[0]);
    }
}
