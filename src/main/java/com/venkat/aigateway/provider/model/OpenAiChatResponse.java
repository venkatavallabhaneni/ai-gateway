package com.venkat.aigateway.provider.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiChatResponse {
  private List<Choice> choices;
  private Usage usage;
  private String model;

  @Data
  @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
  public static class Choice {
    private Message message;
  }

  @Data
  @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
  public static class Message {
    private String role;
    private String content;
  }

  @Data
  @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
  public static class Usage {
    private int prompt_tokens;
    private int completion_tokens;
    private int total_tokens;
  }
}
