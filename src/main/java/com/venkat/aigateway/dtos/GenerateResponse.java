package com.venkat.aigateway.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerateResponse {
  private String model;
  private String text;

  private int promptTokens;
  private int completionTokens;
  private int totalTokens;

  private double estimatedCostUsd;
  private long latencyMs;
}
