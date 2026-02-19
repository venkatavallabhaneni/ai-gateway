package com.venkat.aigateway.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EmbedResponseWithCosineSImilarity {
  private String model;
  private Map<String, Double> cosineSimilarity; 
}
