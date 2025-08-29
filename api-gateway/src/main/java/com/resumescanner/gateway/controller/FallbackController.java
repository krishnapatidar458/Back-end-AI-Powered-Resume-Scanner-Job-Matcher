package com.resumescanner.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        return createFallbackResponse("Authentication service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/users")
    public Mono<ResponseEntity<Map<String, Object>>> usersFallback() {
        return createFallbackResponse("User management service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/resumes")
    public Mono<ResponseEntity<Map<String, Object>>> resumesFallback() {
        return createFallbackResponse("Resume parser service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/jobs")
    public Mono<ResponseEntity<Map<String, Object>>> jobsFallback() {
        return createFallbackResponse("Job matcher service is temporarily unavailable. Please try again later.");
    }

    @GetMapping("/{service}")
    public Mono<ResponseEntity<Map<String, Object>>> genericFallback(@PathVariable String service) {
        return createFallbackResponse(String.format("%s service is temporarily unavailable. Please try again later.", service));
    }

    @GetMapping
    public Mono<ResponseEntity<Map<String, Object>>> defaultFallback() {
        return createFallbackResponse("Service is temporarily unavailable. Please try again later.");
    }

    private Mono<ResponseEntity<Map<String, Object>>> createFallbackResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Service Unavailable");
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("suggestion", "Please check service status and try again in a few moments.");
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}
