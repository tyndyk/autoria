package com.example.auto_ria.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

@Configuration
public class AzureBlobConfiguration {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Bean
    public BlobServiceClient blobServiceClient() {
        if (connectionString == null || connectionString.isEmpty()) {
            throw new IllegalArgumentException("Azure Storage connection string is missing!");
        }

        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }
}
