package com.resumescanner.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
    
    // Simple in-memory rate limiting (in production, use Redis)
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    // Rate limit: 100 requests per minute per IP
    private static final int MAX_REQUESTS = 100;
    private static final Duration TIME_WINDOW = Duration.ofMinutes(1);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String clientIp = getClientIp(exchange);
        
        if (isRateLimited(clientIp)) {
            logger.warn("Rate limit exceeded for IP: {}", clientIp);
            return onRateLimitExceeded(exchange);
        }
        
        return chain.filter(exchange);
    }
    
    private String getClientIp(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String remoteAddr = exchange.getRequest().getRemoteAddress() != null 
            ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() 
            : "unknown";
        
        return remoteAddr;
    }
    
    private boolean isRateLimited(String clientIp) {
        LocalDateTime now = LocalDateTime.now();
        
        rateLimitMap.compute(clientIp, (ip, info) -> {
            if (info == null) {
                return new RateLimitInfo(1, now);
            }
            
            // Reset if time window has passed
            if (Duration.between(info.windowStart, now).compareTo(TIME_WINDOW) >= 0) {
                return new RateLimitInfo(1, now);
            }
            
            // Increment request count
            info.requestCount++;
            return info;
        });
        
        RateLimitInfo info = rateLimitMap.get(clientIp);
        return info.requestCount > MAX_REQUESTS;
    }
    
    private Mono<Void> onRateLimitExceeded(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().add("Content-Type", "application/json");
        response.getHeaders().add("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
        response.getHeaders().add("X-RateLimit-Remaining", "0");
        response.getHeaders().add("Retry-After", String.valueOf(TIME_WINDOW.getSeconds()));
        
        String body = "{\"error\":\"Rate limit exceeded\",\"status\":429,\"message\":\"Too many requests. Please try again later.\"}";
        org.springframework.core.io.buffer.DataBuffer buffer = response.bufferFactory().wrap(body.getBytes());
        
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 1; // Execute after JWT filter
    }
    
    private static class RateLimitInfo {
        int requestCount;
        LocalDateTime windowStart;
        
        RateLimitInfo(int requestCount, LocalDateTime windowStart) {
            this.requestCount = requestCount;
            this.windowStart = windowStart;
        }
    }
}
