package com.vn.hung.xxxpre.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        // This factory uses Apache HttpClient, which is
        // much more reliable for streaming large files.
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory();

        // This factory streams by default with ResponseExtractor,
        // so no "setBufferResponseBody(false)" is needed.

        return new RestTemplate(factory);
    }
}