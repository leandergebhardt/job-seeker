package com.jobseeker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    @Value("${rapidapi.host}")
    private String rapidApiHost;

    @Value("${rapidapi.base-url}")
    private String rapidApiBaseUrl;

    @Bean
    public WebClient rapidApiWebClient() {
        return WebClient.builder()
                .baseUrl(rapidApiBaseUrl)
                .defaultHeader("x-rapidapi-key", rapidApiKey)
                .defaultHeader("x-rapidapi-host", rapidApiHost)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
