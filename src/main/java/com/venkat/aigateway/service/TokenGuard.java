package com.venkat.aigateway.service;

import com.venkat.aigateway.config.AiProviderProperties;
import org.springframework.stereotype.Service;

@Service
public class TokenGuard {
  private final AiProviderProperties props;

  public TokenGuard(AiProviderProperties props) {
    this.props = props;
  }

  public void validatePrompt(String prompt) {
    if (prompt == null || prompt.isBlank()) throw new IllegalArgumentException("prompt is required");
    if (prompt.length() > props.getMaxInputChars()) {
      throw new IllegalArgumentException("prompt too large (chars). Reduce input size.");
    }
  }

  public int clampMaxTokens(Integer requestedMaxTokens) {
    int max = props.getMaxOutputTokens();
    if (requestedMaxTokens == null) return max;
    if (requestedMaxTokens <= 0) throw new IllegalArgumentException("maxTokens must be > 0");
    return Math.min(requestedMaxTokens, max);
  }
}
