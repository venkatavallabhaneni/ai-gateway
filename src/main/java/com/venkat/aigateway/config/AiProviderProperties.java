package com.venkat.aigateway.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "ai.provider")
public class AiProviderProperties {
  private String baseUrl;
  private String apiKey;
  private String chatPath;
  private String embedPath;

  private String defaultChatModel;
  private String defaultEmbedModel;

  private int maxInputChars;
  private int maxOutputTokens;
}
