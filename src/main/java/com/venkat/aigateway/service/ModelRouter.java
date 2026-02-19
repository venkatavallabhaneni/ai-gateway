package com.venkat.aigateway.service;

import com.venkat.aigateway.config.AiProviderProperties;
import org.springframework.stereotype.Service;

@Service
public class ModelRouter {
  private final AiProviderProperties props;

  public ModelRouter(AiProviderProperties props) {
    this.props = props;
  }

  public String selectChatModel(String requestedModel, String intent) {
    if (requestedModel != null && !requestedModel.isBlank()) return requestedModel;

    // very simple policy, evolve later
    if (intent == null) return props.getDefaultChatModel();
    switch (intent.toLowerCase()) {
      case "summary": // choose cheaper later
        return props.getDefaultChatModel();
      case "reasoning": // choose stronger later
        return props.getDefaultChatModel();
      default:
        return props.getDefaultChatModel();
    }
  }

  public String selectEmbedModel(String requestedModel) {
    if (requestedModel != null && !requestedModel.isBlank()) return requestedModel;
    return props.getDefaultEmbedModel();
  }
}
