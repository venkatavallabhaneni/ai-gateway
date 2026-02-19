package com.venkat.aigateway.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmbedResponse {
  private String model;
  private int vectorSize;
  private List<Double> vector; // you can omit in prod; return id instead
  private int promptTokens;
  private double estimatedCostUsd;
  private long latencyMs;
}
