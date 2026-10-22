package com.indielens.logMachine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.indielens.excelConverter.RegisterReader;

public class FileWritter {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy : HH:mm:ss");

    private String nomeArquivo;

    public FileWritter() {
        this(criarNomeArquivo());
    }

    public FileWritter(int idRegistro) {
        this(criarNomeArquivo(idRegistro));
    }

    public FileWritter(Path caminho) {
        this(caminho.toString());
    }

    private FileWritter(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public static FileWritter iniciarExecucao() {
        return new FileWritter("LOG-SEM-REGISTRO-" + LocalDate.now().format(FORMATO_DATA)
            + "-" + UUID.randomUUID() + ".txt");
    }

    public void definirRegistro(int idRegistro) throws IOException {
        Path origem = Path.of(nomeArquivo);
        Path destino = origem.resolveSibling(criarNomeArquivo(idRegistro));
        if (origem.equals(destino)) {
            return;
        }
        try (OutputStream saida = Files.newOutputStream(destino,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            Files.copy(origem, saida);
        }
        Files.delete(origem);
        nomeArquivo = destino.toString();
    }

    //cria uma string para o nomme do arquivo
    public static String criarNomeArquivo() {
        int idRegistro = new RegisterReader().getRegistro() + 1;
        return criarNomeArquivo(idRegistro);
    }

    public static String criarNomeArquivo(int idRegistro) {
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
                return "Execução da aplicação";
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
                System.out.println("Arquivo de Log existente: " + arquivo.getName());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível criar o arquivo de log", e);
        }
    }

    //cria as linhas dentro do log
    public void CriarRegistro(int local, String texto, boolean sucesso) {
        String registro = formatarRegistro(local, texto, sucesso);
        System.out.println(registro);

        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(nomeArquivo), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) { // Cria caso não exista, e adiciona linhas ao final, ignorando anteriores
            bw.write(registro);
            bw.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível escrever no arquivo de log", e);
        }
    }

    public static String formatarRegistro(int local, String texto, boolean sucesso) {
        return "[" + buscarDataHora() + "] [" + buscarLocal(local) + "] ["
            + texto.replaceAll("\\R", " ") + "] - [" + (sucesso ? "Sucesso" : "Erro") + "]";
    }
}
