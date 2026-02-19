package com.venkat.aigateway.provider.model;

import lombok.Data;

import java.util.List;

@Data
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiEmbeddingResponse {
  private String model;
  private List<Item> data;
  private Usage usage;

  @Data
  @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
  public static class Item {
    private List<Double> embedding;
    private int index;
  }

  @Data
  @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
  public static class Usage {
    private int prompt_tokens;
    private int total_tokens;
  }
}
