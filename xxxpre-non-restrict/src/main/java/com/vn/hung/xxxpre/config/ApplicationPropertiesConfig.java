package com.vn.hung.xxxpre.config;



import com.vn.hung.xxxpre.model.ApplicationIntegrationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationPropertiesConfig {
    @Bean
    @ConfigurationProperties(
            prefix = "application.integration"
    )
    public ApplicationIntegrationProperties applicationIntegrationProperties() {
        return new ApplicationIntegrationProperties();
    }
}
