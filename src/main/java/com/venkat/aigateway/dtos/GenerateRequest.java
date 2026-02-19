package com.venkat.aigateway.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class GenerateRequest {
  @NotBlank
  private String prompt;

  // optional overrides
  private String model;

  @Positive
  private Integer maxTokens;

  private Double temperature;

  // optional hint for routing like: "reasoning", "summary", "chat"
  private String intent;
}
