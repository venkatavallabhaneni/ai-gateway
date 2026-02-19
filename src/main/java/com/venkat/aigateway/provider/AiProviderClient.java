package com.venkat.aigateway.provider;

import com.venkat.aigateway.provider.model.OpenAiChatRequest;
import com.venkat.aigateway.provider.model.OpenAiChatResponse;
import com.venkat.aigateway.provider.model.OpenAiEmbeddingRequest;
import com.venkat.aigateway.provider.model.OpenAiEmbeddingResponse;

public interface AiProviderClient {
  OpenAiChatResponse chat(OpenAiChatRequest req);
  OpenAiEmbeddingResponse embed(OpenAiEmbeddingRequest req);
  String providerName();
}
