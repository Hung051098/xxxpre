package com.vn.hung.xxxpre.config;

import com.vn.hung.xxxpre.repository.ApplicationIntegrationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

@Configuration
public class DynamoDbConfig {


    @Autowired
    private ApplicationIntegrationProperties applicationIntegrationProperties;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        ApplicationIntegrationProperties.Aws aws = this.applicationIntegrationProperties.getAws();
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(aws.getAccessKey(), aws.getSecretAccessKey());
        return (DynamoDbClient)((DynamoDbClientBuilder)((DynamoDbClientBuilder)DynamoDbClient.builder().credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))).region(Region.of(aws.getRegion()))).build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(this.dynamoDbClient()).build();
    }

}