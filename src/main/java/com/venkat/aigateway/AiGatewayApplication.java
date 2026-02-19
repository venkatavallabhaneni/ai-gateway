package com.venkat.aigateway;

import com.venkat.aigateway.config.AiProviderProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AiProviderProperties.class)
public class AiGatewayApplication {
  public static void main(String[] args) {
    SpringApplication.run(AiGatewayApplication.class, args);
  }
}
