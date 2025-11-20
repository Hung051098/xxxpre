package com.vn.hung.xxxpre.config;

import com.vn.hung.xxxpre.model.ApplicationIntegrationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDbConfig {


    @Autowired
    private ApplicationIntegrationProperties applicationIntegrationProperties;


}