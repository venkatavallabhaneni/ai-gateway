package com.venkat.aigateway.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CostService {

  // USD per 1M tokens (example placeholders – update with your provider pricing)
  private static final Map<String, Double> INPUT_USD_PER_1M = Map.of(
      "gpt-4o-mini", 0.15,
      "gpt-4o", 5.00,
      "text-embedding-3-large", 0.13
  );

  private static final Map<String, Double> OUTPUT_USD_PER_1M = Map.of(
      "gpt-4o-mini", 0.60,
      "gpt-4o", 15.00
  );

  public double estimateChatCostUsd(String model, int promptTokens, int completionTokens) {
    double inRate = INPUT_USD_PER_1M.getOrDefault(model, 1.0);
    double outRate = OUTPUT_USD_PER_1M.getOrDefault(model, 1.0);
    return (promptTokens / 1_000_000.0) * inRate + (completionTokens / 1_000_000.0) * outRate;
  }

  public double estimateEmbedCostUsd(String model, int promptTokens) {
    double inRate = INPUT_USD_PER_1M.getOrDefault(model, 0.5);
    return (promptTokens / 1_000_000.0) * inRate;
  }
}
