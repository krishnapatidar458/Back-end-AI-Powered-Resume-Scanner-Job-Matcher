package com.resumescanner.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    // Public endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/auth/signup",
        "/api/auth/signin",
        "/api/auth/refresh-token",
        "/actuator/health",
        "/v3/api-docs",
        "/swagger-ui",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/webjars",
        "/webjars/**",
        "/user-service/swagger-ui",
        "/user-service/swagger-ui/**",
        "/user-service/v3/api-docs",
        "/user-service/v3/api-docs/**",
        "/resume-service/swagger-ui",
        "/resume-service/swagger-ui/**",
        "/resume-service/v3/api-docs",
        "/resume-service/v3/api-docs/**",
        "/job-service/swagger-ui",
        "/job-service/swagger-ui/**",
        "/job-service/v3/api-docs",
        "/job-service/v3/api-docs/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        logger.debug("Processing request for path: {}", path);

        // Check if the endpoint is public
        if (isPublicEndpoint(path)) {
            logger.debug("Public endpoint accessed: {}", path);
            return chain.filter(exchange);
        }

        // Extract JWT token from Authorization header
        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header for path: {}", path);
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        // For now, we'll do basic validation. In production, you'd validate with JWT library
        if (token.isEmpty()) {
            logger.warn("Empty JWT token for path: {}", path);
            return onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
        }

        // Add user info to request headers for downstream services
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
            .header("X-User-Token", token)
            .build();

        logger.debug("JWT token validated successfully for path: {}", path);

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");

        String body = String.format("{\"error\":\"%s\",\"status\":%d}", message, status.value());
        org.springframework.core.io.buffer.DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0; // Execute after logging filter
    }
}
