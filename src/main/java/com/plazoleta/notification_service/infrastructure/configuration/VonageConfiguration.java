package com.plazoleta.notification_service.infrastructure.configuration;

import com.vonage.client.VonageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VonageConfiguration {

    @Value("${vonage.api-key}")
    private String apiKey;

    @Value("${vonage.api-secret}")
    private String apiSecret;

    @Bean
    public VonageClient  vonageClient() {
        return  VonageClient.builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();
    }
}
