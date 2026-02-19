package com.venkat.aigateway.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmbedSetRequest {
  @NotBlank
  private String[] texts;
  private String model;
}
