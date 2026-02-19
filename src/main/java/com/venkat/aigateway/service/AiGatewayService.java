package com.venkat.aigateway.service;

import com.venkat.aigateway.dtos.*;
import com.venkat.aigateway.provider.AiProviderClient;
import com.venkat.aigateway.provider.OpenAiClient;
import com.venkat.aigateway.provider.model.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiGatewayService {
  private static final Logger log = LoggerFactory.getLogger(AiGatewayService.class);

  private final OpenAiClient openAiClient;
  private final ModelRouter modelRouter;
  private final TokenGuard tokenGuard;
  private final CostService costService;
  private final AiProviderClient providerClient;

  public GenerateResponse generate(GenerateRequest req) {
    long start = System.currentTimeMillis();

    tokenGuard.validatePrompt(req.getPrompt());
    int maxTokens = tokenGuard.clampMaxTokens(req.getMaxTokens());
    String model = modelRouter.selectChatModel(req.getModel(), req.getIntent());
    double temperature = req.getTemperature() != null ? req.getTemperature() : 0.2;

    OpenAiChatRequest providerReq = OpenAiChatRequest.builder()
        .model(model)
        .max_tokens(maxTokens)
        .temperature(temperature)
        .messages(List.of(
            OpenAiChatRequest.Message.builder().role("system").content("You are a helpful assistant.").build(),
            OpenAiChatRequest.Message.builder().role("user").content(req.getPrompt()).build()
        ))
        .build();

    // OpenAiChatResponse providerResp = openAiClient.chat(providerReq);
    OpenAiChatResponse providerResp = providerClient.chat(providerReq);
    

    String text = (providerResp.getChoices() != null && !providerResp.getChoices().isEmpty())
        ? providerResp.getChoices().get(0).getMessage().getContent()
        : "";

    int promptTokens = providerResp.getUsage() != null ? providerResp.getUsage().getPrompt_tokens() : 0;
    int completionTokens = providerResp.getUsage() != null ? providerResp.getUsage().getCompletion_tokens() : 0;
    int totalTokens = providerResp.getUsage() != null ? providerResp.getUsage().getTotal_tokens() : (promptTokens + completionTokens);

    double cost = costService.estimateChatCostUsd(model, promptTokens, completionTokens);
    long latencyMs = System.currentTimeMillis() - start;

    // Structured log (good enough for Day 2; Day 7 we’ll formalize observability)
    log.info("ai.generate model={} promptTokens={} completionTokens={} totalTokens={} costUsd={} latencyMs={}",
        model, promptTokens, completionTokens, totalTokens, cost, latencyMs);

    return GenerateResponse.builder()
        .model(model)
        .text(text)
        .promptTokens(promptTokens)
        .completionTokens(completionTokens)
        .totalTokens(totalTokens)
        .estimatedCostUsd(cost)
        .latencyMs(latencyMs)
        .build();
  }

  public EmbedResponse embed(EmbedRequest req) {
    long start = System.currentTimeMillis();

    tokenGuard.validatePrompt(req.getText());
    String model = modelRouter.selectEmbedModel(req.getModel());

    OpenAiEmbeddingRequest providerReq = OpenAiEmbeddingRequest.builder()
        .model(model)
        .input(req.getText())
        .build();

    OpenAiEmbeddingResponse providerResp = openAiClient.embed(providerReq);

    List<Double> vector = (providerResp.getData() != null && !providerResp.getData().isEmpty())
        ? providerResp.getData().get(0).getEmbedding()
        : List.of();

    int promptTokens = providerResp.getUsage() != null ? providerResp.getUsage().getPrompt_tokens() : 0;
    double cost = costService.estimateEmbedCostUsd(model, promptTokens);
    long latencyMs = System.currentTimeMillis() - start;

    log.info("ai.embed model={} promptTokens={} vectorSize={} costUsd={} latencyMs={}",
        model, promptTokens, vector.size(), cost, latencyMs);

    return EmbedResponse.builder()
        .model(model)
        .vectorSize(vector.size())
        .vector(vector)
        .promptTokens(promptTokens)
        .estimatedCostUsd(cost)
        .latencyMs(latencyMs)
        .build();
  }
}
