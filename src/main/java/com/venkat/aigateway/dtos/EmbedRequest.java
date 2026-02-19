package com.venkat.aigateway.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmbedRequest {
  @NotBlank
  private String text;

  // optional override
  private String model;
}
