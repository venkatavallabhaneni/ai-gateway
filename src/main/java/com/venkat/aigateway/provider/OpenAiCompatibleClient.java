package com.venkat.aigateway.provider;

import com.venkat.aigateway.config.AiProviderProperties;
import com.venkat.aigateway.provider.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OpenAiCompatibleClient implements AiProviderClient {
  private static final Logger log = LoggerFactory.getLogger(OpenAiCompatibleClient.class);

  private final OkHttpClient http = new OkHttpClient();
  private final ObjectMapper om = new ObjectMapper();
  private final AiProviderProperties props;

  public OpenAiCompatibleClient(AiProviderProperties props) {
    this.props = props;
  }

  @Override
  public String providerName() {
    return "openai-compatible";
  }

  @Override
  @RateLimiter(name = "llmProvider")
  @CircuitBreaker(name = "llmProvider", fallbackMethod = "chatFallback")
  @Retry(name = "llmProvider")
  @Bulkhead(name = "llmProvider")
  public OpenAiChatResponse chat(OpenAiChatRequest req) {
    return post(props.getChatPath(), req, OpenAiChatResponse.class);
  }

  @Override
  @RateLimiter(name = "llmProvider")
  @CircuitBreaker(name = "llmProvider", fallbackMethod = "embedFallback")
  @Retry(name = "llmProvider")
  @Bulkhead(name = "llmProvider")
  public OpenAiEmbeddingResponse embed(OpenAiEmbeddingRequest req) {
    return post(props.getEmbedPath(), req, OpenAiEmbeddingResponse.class);
  }

  // ---- FALLBACKS ----
  // Signature must match: (originalArgs..., Throwable)
  private OpenAiChatResponse chatFallback(OpenAiChatRequest req, Throwable t) {
    log.warn("chatFallback triggered provider={} reason={}", providerName(), t.toString());
    // Return a controlled “degraded” response (don’t throw raw errors to clients)
    OpenAiChatResponse resp = new OpenAiChatResponse();
    OpenAiChatResponse.Choice choice = new OpenAiChatResponse.Choice();
    OpenAiChatResponse.Message msg = new OpenAiChatResponse.Message();
    msg.setRole("assistant");
    msg.setContent("AI service is temporarily unavailable. Please retry in a moment.");
    choice.setMessage(msg);
    resp.setChoices(java.util.List.of(choice));
    resp.setModel(req.getModel());
    OpenAiChatResponse.Usage usage = new OpenAiChatResponse.Usage();
    usage.setPrompt_tokens(0);
    usage.setCompletion_tokens(0);
    usage.setTotal_tokens(0);
    resp.setUsage(usage);
    return resp;
  }

  private OpenAiEmbeddingResponse embedFallback(OpenAiEmbeddingRequest req, Throwable t) {
    log.warn("embedFallback triggered provider={} reason={}", providerName(), t.toString());
    OpenAiEmbeddingResponse resp = new OpenAiEmbeddingResponse();
    resp.setModel(req.getModel());
    OpenAiEmbeddingResponse.Usage usage = new OpenAiEmbeddingResponse.Usage();
    usage.setPrompt_tokens(0);
    usage.setTotal_tokens(0);
    resp.setUsage(usage);
    resp.setData(java.util.List.of());
    return resp;
  }

  // ---- HTTP POST ----
  private <T> T post(String path, Object body, Class<T> clazz) {
    String url = props.getBaseUrl() + path;
    try {
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
          throw new IOException("Provider error: " + response.code() + " " + err);
        }
        String respJson = response.body() != null ? response.body().string() : "{}";
        return om.readValue(respJson, clazz);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
