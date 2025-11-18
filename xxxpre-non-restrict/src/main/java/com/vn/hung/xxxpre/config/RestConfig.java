package com.vn.hung.xxxpre.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestConfig {
    @Bean({"restTemplate"})
    public RestTemplate restTemplate(@Qualifier("customeMappingJackson2HttpMessageConverter") MappingJackson2HttpMessageConverter converter) {
        RestTemplate restTemplate = new RestTemplate(this.clientHttpRequestFactory());
        converter.setObjectMapper(this.objectMapper());
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectionRequestTimeout(60000);
        clientHttpRequestFactory.setConnectTimeout(60000);
        return clientHttpRequestFactory;
    }

    @Bean(
            name = {"customeMappingJackson2HttpMessageConverter"}
    )
    public MappingJackson2HttpMessageConverter converter(@Qualifier("SLobjectMapper") ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        jacksonConverter.setObjectMapper(objectMapper);
        jacksonConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        return jacksonConverter;
    }

    @Bean(
            name = {"SLobjectMapper"}
    )
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }
}