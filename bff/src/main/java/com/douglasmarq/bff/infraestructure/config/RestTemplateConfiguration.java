package com.douglasmarq.bff.infraestructure.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean(name = "imageServiceRestTemplate")
    public RestTemplate imageServiceRestTemplate(
            @Value("${bff.api.image-service.timeout.connect:100}") int connectTimeout,
            @Value("${bff.api.image-service.timeout.read:100}") int readTimeout,
            @Value("${bff.api.image-service.max-connections:10}") int maxConnections) {
        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxConnections);
        connectionManager.setDefaultMaxPerRoute(maxConnections);

        RequestConfig requestConfig =
                RequestConfig.custom()
                        .setConnectionRequestTimeout(Timeout.ofSeconds(connectTimeout))
                        .setResponseTimeout(Timeout.ofSeconds(readTimeout))
                        .build();

        CloseableHttpClient httpClient =
                HttpClientBuilder.create()
                        .setConnectionManager(connectionManager)
                        .setDefaultRequestConfig(requestConfig)
                        .build();

        RestTemplate restTemplate =
                new RestTemplateBuilder()
                        .requestFactory(
                                () -> new HttpComponentsClientHttpRequestFactory(httpClient))
                        .build();

        return restTemplate;
    }
}
