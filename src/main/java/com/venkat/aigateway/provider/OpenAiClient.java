package com.venkat.aigateway.provider;

import com.venkat.aigateway.config.AiProviderProperties;
import com.venkat.aigateway.provider.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OpenAiClient {

  private final OkHttpClient http = new OkHttpClient();
  private final ObjectMapper om = new ObjectMapper();
  private final AiProviderProperties props;

  public OpenAiClient(AiProviderProperties props) {
    this.props = props;
  }

  public OpenAiChatResponse chat(OpenAiChatRequest req) {
    return post(props.getChatPath(), req, OpenAiChatResponse.class);
  }

  public OpenAiEmbeddingResponse embed(OpenAiEmbeddingRequest req) {
    return post(props.getEmbedPath(), req, OpenAiEmbeddingResponse.class);
  }

  private <T> T post(String path, Object body, Class<T> clazz) {
    try {
      String url = props.getBaseUrl() + path;
      String json = om.writeValueAsString(body);

      Request request = new Request.Builder()
          .url(url)
          .addHeader("Authorization", "Bearer " + props.getApiKey())
          .addHeader("Content-Type", "application/json")
          .post(RequestBody.create(json, MediaType.parse("application/json")))
          .build();

      try (Response response = http.newCall(request).execute()) {
        if (!response.isSuccessful()) {
          String err = response.body() != null ? response.body().string() : "";
          throw new RuntimeException("LLM provider error: " + response.code() + " " + err);
        }
        String respJson = response.body() != null ? response.body().string() : "{}";
        return om.readValue(respJson, clazz);
      }
    } catch (IOException e) {
      throw new RuntimeException("Provider call failed", e);
    }
  }
}
