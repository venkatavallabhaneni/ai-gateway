package com.venkat.aigateway.controller;

import com.venkat.aigateway.dtos.*;
import com.venkat.aigateway.service.AiGatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiGatewayController {

    private final AiGatewayService aiGatewayService;

    @PostMapping("/generate")
    public GenerateResponse generate(@Valid @RequestBody GenerateRequest req) {
        return aiGatewayService.generate(req);
    }

    @PostMapping("/embed")
    public EmbedResponse embed(@Valid @RequestBody EmbedRequest req) {
        return aiGatewayService.embed(req);
    }
}
