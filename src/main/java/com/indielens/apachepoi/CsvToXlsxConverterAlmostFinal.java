package com.indielens.apachepoi;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
	
public class CsvToXlsxConverterAlmostFinal {
	
	//PONTO IMPORTANTE: antes de converter abrir o CSV e entre Price e About the game vai ter um campo : DiscountDLC count.
	// Tem que colocar uma virgula entre o t e o D maiusculo para que fiquem 40 campos, caso não coloque não tem problema
	// mas vai disparar um aviso na linha 1 e vai ter que editar manualmente a tabela para dividir esses dois campos na linha 1 antes de converter para o BD
	public static void main(String[] args) {
        String arquivoCSV = "C:/Users/vixus/Downloads/games.csv"; // ← Colocar o caminho absoluto do arquivo, exemplo: "C:/Users/vixus/Downloads/games (2).csv"
        String arquivoXLSX = "C:/Users/vixus/Downloads/ResultadoConvertido5.xlsx";// ← Colocar o caminho absoluto onde a tabela XLSX será colocada, exemplo: "C:/Users/vixus/Downloads/ResultadoConvertido.xlsx" ← tem que ter o .xlxs
        int totalLinhas = 0;
        
        // Ignora os comentários, no script de BD da pra enquanto lê os campos fazer essa divisão ex: if(texto for igual a DiscountDLC count) ai faz um substring lá

        //tentando criar um workbook com 100 linhas por vez.
        //se tentar criar as 120mil linhas de uma vez vai simplesmente EXPLODIR sua IDE
        //(acredite em mim, crashou algumas vezes aqui até saber disso...)
        
        //NOVAMENTE!!!! IMPORTANTE
        // por que estou usando SXSSFWorkbook ao invés do XSSFWorkbook????
        //simplesmente pelo citado acima, o XSSFWorkbook deixa as linhas em memoria, o que pode LASCAR teu pc inteirinho, 
        // seu programa pode pegar tipo 10gb de ram só com registro carregado
        // já com SXSSFWorkbook ele carrega 100 linhas na ram, depois na linha 101 ele descarrega a 001 e assim por diante
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
        	
        	//criando um leitorCSV, vai ler o arquivo csv :P
            try (CSVReader leitorCSV = new CSVReaderBuilder(
            		new InputStreamReader( // isso aqui pega os bytes do arquivo e transforma em caracteres
            				new FileInputStream(arquivoCSV),StandardCharsets.UTF_8) // abre o arquivo "ArquivoCSV" usando o padrão UTF8 para uma leitura dos bytes
            		).withCSVParser( // aqui estamos escolhendo como ler o arquivo usando csvReader, nesse caso fazendo o "Parsing"
            				new RFC4180Parser() // tudo o que essa linha faz é definir que o padrão é o RFC 4180, que por padrão usa vírgulas como separadores e aspas duplas para delimitar campos
            				).build() // só termina de criar o builder ali em cima
            		) {

                Sheet sheet = workbook.createSheet("Dados");
                String[] linhas;

                System.out.println("Iniciando conversão");

                //valida se existe uma próxima linha para ser lida
                while ((linhas = leitorCSV.readNext()) != null) {
                    if (totalLinhas >= 1048576) {
                        throw new IOException("O CSV excede o limite de linhas da planilha.");
                        //sim o excel possui limite de linhas, esse erro só vai dar trigger se por algum motivo a , do perser quebrar
                        //ai ele iria ir "enchendo" todas as células adjacentes em linhas com valores que provavelmente serão nulos
                    }

                    if (linhas.length != 40) {
                        System.out.println("Aviso: registro " + (totalLinhas + 1) + " possui " + linhas.length + " colunas; esperadas: 40.");
                        //erro citado acima
                    }

                    //cria a próxima linha
                    Row linhaAtual = sheet.createRow(totalLinhas);
                    
                    // Grava todos os campos do registro, até o limite de 16.384 colunas.
                    //isso evita muitos erros chatos
                    //pra descobrir isso fui indo até o final das colunas...
                    int colunas = Math.min(linhas.length, 16384);

                    for (int i = 0; i < colunas; i++) {
                        String valor = linhas[i];

                        if (valor != null && valor.length() > 32767) {
                            System.out.println("Aviso: texto cortado no registro "+ (totalLinhas + 1) + ", coluna " + (i + 1) + ".");
                            //esse erro vai ser principalmente disparado na coluna 10, a "About the game"
                            //isso se deve ao limite de 32767 caracteres por célula no excel, descrição longa pra bixo, chat gpt resuma pa nois
                            valor = valor.substring(0, 32767);
                        }

                        //seta o valor desejado para a linha e coluna
                        linhaAtual.createCell(i).setCellValue(valor);
                    }

                    // avisa que tá jogando fora todos os caracteres adicionais desse campo pq passou do limite
                    if (linhas.length > 16384) {
                        System.out.println("Aviso: colunas excedentes descartadas no registro "+ (totalLinhas + 1) + ".");
                    }

                    totalLinhas++;
                }

                // Abre o arquivo de saída somente depois de ler o CSV.
                try (FileOutputStream saida = new FileOutputStream(arquivoXLSX)) {
                    workbook.write(saida);
                }
                
            // finally : de forma resumida ele SEMPRE executa depois de um try catch, independente se deu true no try ou se deu catch
            } finally {
                // Remove os arquivos temporários no disco.
                workbook.dispose();
            }

            System.out.println("Conversão concluída. Total de registros: " + totalLinhas);

        // aqui to usando o | se chama Bitwise OR, isto é, ele é um pouco diferente de ||
            // Trata ambos os IOException ou CsvValidationException usando o mesmo bloco.
            // asim não temos que fazer dois blocos de catch, isso se chama multi-catch
        } catch (IOException | CsvValidationException e) {
            System.err.println("Erro na conversão: " + e.getMessage());
        }
    }
}