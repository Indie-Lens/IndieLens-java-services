package com.indielens.apachepoi;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.exceptions.CsvValidationException;

import io.github.cdimascio.dotenv.Dotenv;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
	
public class CsvToXlsxConverterFinal {
	public static void main(String[] args) {
		Dotenv env = Dotenv.load();
		
        String arquivoCSV = env.get("CSV_PATH");
        String arquivoXLSX = env.get("XLSX_PATH");
        int totalLinhas = 0;
        
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
        	
            try (CSVReader leitorCSV = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(arquivoCSV),StandardCharsets.UTF_8)).withCSVParser(new RFC4180Parser()).build()) {

                Sheet sheet = workbook.createSheet("Dados");
                String[] linhas;

                System.out.println("Iniciando conversão");

                while ((linhas = leitorCSV.readNext()) != null) {
                    if (totalLinhas >= 1048576) {
                        throw new IOException("O CSV excede o limite de linhas da planilha.");
                    }

                    if (linhas.length != 40) {
                        System.out.println("Aviso: registro " + (totalLinhas + 1) + " possui " + linhas.length + " colunas; esperadas: 40.");
                    }

                    Row linhaAtual = sheet.createRow(totalLinhas);

                    int colunas = Math.min(linhas.length, 16384);

                    for (int i = 0; i < colunas; i++) {
                        String valor = linhas[i];

                        if (valor != null && valor.length() > 32767) {
                            System.out.println("Aviso: texto cortado no registro "+ (totalLinhas + 1) + ", coluna " + (i + 1) + ".");
                            valor = valor.substring(0, 32767);
                        }

                        linhaAtual.createCell(i).setCellValue(valor);
                    }
                    
                    if (linhas.length > 16384) {
                        System.out.println("Aviso: colunas excedentes descartadas no registro "+ (totalLinhas + 1) + ".");
                    }

                    totalLinhas++;
                }

                try (FileOutputStream saida = new FileOutputStream(arquivoXLSX)) {
                    workbook.write(saida);
                }
                
            } finally {
                workbook.dispose();
            }

            System.out.println("Conversão concluída. Total de registros: " + totalLinhas);
            
        } catch (IOException | CsvValidationException e) {
            System.err.println("Erro na conversão: " + e.getMessage());
        }
    }
}