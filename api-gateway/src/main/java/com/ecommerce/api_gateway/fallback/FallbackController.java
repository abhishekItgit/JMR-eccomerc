package com.ecommerce.api_gateway.fallback;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public Mono<Map<String, Object>> fallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Service temporarily unavailable, please try later.");
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        return Mono.just(response);
    }
}

