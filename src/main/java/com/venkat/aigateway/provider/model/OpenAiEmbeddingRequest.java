package com.venkat.aigateway.provider.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAiEmbeddingRequest {
  private String model;
  private String input;
}
