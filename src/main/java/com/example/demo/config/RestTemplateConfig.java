package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder; // <-- ВАЖНО: новый пакет
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${kinopoisk.api.token}")
    private String apiToken;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add("X-API-KEY", apiToken);
            return execution.execute(request, body);
        };

        return builder
                .additionalInterceptors(interceptor)
                .build();
    }
}