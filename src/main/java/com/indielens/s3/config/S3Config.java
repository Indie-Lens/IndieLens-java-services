package com.indielens.s3.config;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public final class S3Config {

    private S3Config() {
    }

    public static S3Client criarCliente() {
        String regiao = System.getenv("AWS_REGION");

        if (regiao == null || regiao.isBlank()) {
            throw new IllegalStateException("Configure AWS_REGION.");
        }

        return S3Client.builder().region(Region.of(regiao)).build();
    }
}