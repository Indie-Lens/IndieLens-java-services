package com.indielens.apachepoi;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.exceptions.CsvValidationException;
import com.indielens.logMachine.FileWritter;

import io.github.cdimascio.dotenv.Dotenv;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
	
public class CsvToXlsxConverterFinal {
	public static void main(String[] args) {
		Dotenv env = Dotenv.load();
        try {
            converter(env.get("CSV_PATH"), env.get("XLSX_PATH"), null);
        } catch (IOException | CsvValidationException e) {
            System.err.println("Erro na conversão: " + e.getMessage());
        }
    }

    public static int converter(String arquivoCSV, String arquivoXLSX, FileWritter log)
            throws IOException, CsvValidationException {
        int totalLinhas = 0;

        if (arquivoCSV == null || arquivoCSV.trim().isEmpty() || !new File(arquivoCSV).isFile()) {
            throw new IOException("CSV_PATH deve apontar para um arquivo CSV existente, incluindo o nome e a extensao .csv.");
        }
        if (arquivoXLSX == null || arquivoXLSX.trim().isEmpty()) {
            throw new IOException("XLSX_PATH deve indicar o arquivo de destino.");
        }
        if (new File(arquivoCSV).getCanonicalFile().equals(new File(arquivoXLSX).getCanonicalFile())) {
            throw new IOException("O destino XLSX deve ser diferente do arquivo CSV de entrada.");
        }
        
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
        	
            try (CSVReader leitorCSV = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(arquivoCSV),StandardCharsets.UTF_8)).withCSVParser(new RFC4180Parser()).build()) {

                Sheet sheet = workbook.createSheet("Dados");
                String[] linhas;

                registrar(log, "Iniciando conversão do arquivo CSV para XLSX", true);

                while ((linhas = leitorCSV.readNext()) != null) {
                    if (totalLinhas >= 1048576) {
                        throw new IOException("O CSV excede o limite de linhas da planilha.");
                    }

                    if (linhas.length != 40) {
                        registrar(log, "Aviso: registro " + (totalLinhas + 1) + " possui " + linhas.length + " colunas; esperadas: 40.", false);
                    }

                    Row linhaAtual = sheet.createRow(totalLinhas);

                    int colunas = Math.min(linhas.length, 16384);

                    for (int i = 0; i < colunas; i++) {
                        String valor = linhas[i];

                        if (valor != null && valor.length() > 32767) {
                            registrar(log, "Aviso: texto cortado no registro "+ (totalLinhas + 1) + ", coluna " + (i + 1) + ".", false);
                            valor = valor.substring(0, 32767);
                        }

                        linhaAtual.createCell(i).setCellValue(valor);
                    }
                    
                    if (linhas.length > 16384) {
                        registrar(log, "Aviso: colunas excedentes descartadas no registro "+ (totalLinhas + 1) + ".", false);
                    }

                    totalLinhas++;
                }

                try (FileOutputStream saida = new FileOutputStream(arquivoXLSX)) {
                    workbook.write(saida);
                }
                
            } finally {
                workbook.dispose();
            }

        }
        registrar(log, "Conversão concluída. Total de linhas: " + totalLinhas, true);
        return totalLinhas;
    }

    private static void registrar(FileWritter log, String texto, boolean sucesso) {
        if (log == null) {
            System.out.println(FileWritter.formatarRegistro(2, texto, sucesso));
        } else {
            log.CriarRegistro(2, texto, sucesso);
        }
    }
}
