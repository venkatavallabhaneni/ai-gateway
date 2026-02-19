package com.venkat.aigateway.provider.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OpenAiChatRequest {
  private String model;
  private List<Message> messages;
  private Integer max_tokens;
  private Double temperature;

  @Data
  @Builder
  public static class Message {
    private String role;    // "user", "system"
    private String content;
  }
}
