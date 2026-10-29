package com.indielens.s3.service;

import java.io.InputStream;
import java.nio.file.Path;
import java.math.BigInteger;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3Service {

    private final S3Client cliente;
    private final String bucket;

    public S3Service(S3Client cliente, String bucket) {
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalArgumentException("Configure S3_BUCKET.");
        }

        this.cliente = cliente;
        this.bucket = bucket;
    }

    public void enviarArquivo(Path arquivo, String chave) {
        PutObjectRequest requisicao = PutObjectRequest.builder()
                .bucket(bucket)
                .key(chave)
                .build();

        cliente.putObject(requisicao, RequestBody.fromFile(arquivo));
    }

    public InputStream abrirArquivo(String chave) {
        GetObjectRequest requisicao = GetObjectRequest.builder()
                .bucket(bucket)
                .key(chave)
                .build();

        return cliente.getObject(requisicao);
    }

    public String selecionarCsv(int ultimoRegistro) {
        String prefixo = "Brute/games-";
        String esperado = prefixo + ultimoRegistro + ".csv";
        String maiorChave = null;
        BigInteger maiorNumero = null;
        boolean encontrouEsperado = false;
        ListObjectsV2Request requisicao = ListObjectsV2Request.builder()
                .bucket(bucket).prefix(prefixo).build();

        for (var objeto : cliente.listObjectsV2Paginator(requisicao).contents()) {
            String chave = objeto.key();
            String numero = chave.substring(prefixo.length());
            if (!numero.matches("(0|[1-9][0-9]*)\\.csv")) {
                continue;
            }
            BigInteger valor = new BigInteger(numero.substring(0, numero.length() - 4));
            if (chave.equals(esperado)) {
                encontrouEsperado = true;
            }
            if (maiorNumero == null || valor.compareTo(maiorNumero) > 0) {
                maiorNumero = valor;
                maiorChave = chave;
            }
        }
        if (encontrouEsperado) {
            return esperado;
        }
        if (maiorChave == null) {
            throw new IllegalStateException("Nenhum CSV games-X.csv encontrado em Brute/.");
        }
        return maiorChave;
    }
}
