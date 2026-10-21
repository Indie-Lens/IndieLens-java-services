package com.indielens.logMachine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.indielens.excelConverter.RegisterReader;

public class FileWritter {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy : HH:mm:ss");

    private final String nomeArquivo;

    public FileWritter() {
        nomeArquivo = criarNomeArquivo();
    }

    //cria uma string para o nomme do arquivo
    public static String criarNomeArquivo() {
        int idRegistro = new RegisterReader().getRegistro() + 1;
        return "LOG-" + idRegistro + "-" + LocalDate.now().format(FORMATO_DATA) + ".txt";
    }

    public static String buscarDataHora() {
        return LocalDateTime.now().format(FORMATO_DATA_HORA);
    }

    //será utilizado para debug
    public static String buscarLocal(int local) {
        switch (local) {
            case 1:
                return "Validação de conexões";
            case 2:
                return "Extração de dados";
            case 3:
                return "Leitura de dados";
            case 4:
                return "Insert no banco de dados";
            case 5:
                return "Leitura de dados";
            default:
                return "ERRO: int invalida em buscarLocal()";
        }
    }

    //cria o arquivo de log
    public void criarLog() {
        try {
            File arquivo = new File(nomeArquivo);
            if (arquivo.createNewFile()) {
                System.out.println("Arquivo de Log criado: " + arquivo.getName());
            } else {
                System.out.println("Erro : arquivo já existente.");
            }
        } catch (IOException e) {
            System.out.println("Ocorreu um erro ao tentar criar o arquivo: " + e.getMessage());
        }
    }

    //cria as linhas dentro do log
    public void CriarRegistro(int local, String texto, boolean sucesso) {
        String registro = "[" + buscarDataHora() + "] [" + buscarLocal(local) + "] [" + texto + "] - [" + (sucesso ? "Sucesso" : "Erro") + "]";

        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(nomeArquivo), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) { // Cria caso não exista, e adiciona linhas ao final, ignorando anteriores
            bw.write(registro);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }
}
