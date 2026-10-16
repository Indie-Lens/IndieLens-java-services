package com.indielens.s3.service;

import java.io.InputStream;
import java.nio.file.Path;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
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
}