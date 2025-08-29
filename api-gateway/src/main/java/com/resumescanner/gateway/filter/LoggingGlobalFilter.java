package com.resumescanner.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingGlobalFilter.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Log request details
        logger.info("=== Incoming Request ===");
        logger.info("Timestamp: {}", LocalDateTime.now().format(formatter));
        logger.info("Method: {}", request.getMethod());
        logger.info("URI: {}", request.getURI());
        logger.info("Path: {}", request.getPath().value());
        logger.info("Remote Address: {}", request.getRemoteAddress());
        logger.info("Headers: {}", request.getHeaders());

        // Record start time
        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Log response details
            logger.info("=== Outgoing Response ===");
            logger.info("Status Code: {}", response.getStatusCode());
            logger.info("Response Headers: {}", response.getHeaders());
            logger.info("Duration: {} ms", duration);
            logger.info("===========================");
        }));
    }

    @Override
    public int getOrder() {
        return -1; // High priority
    }
}
