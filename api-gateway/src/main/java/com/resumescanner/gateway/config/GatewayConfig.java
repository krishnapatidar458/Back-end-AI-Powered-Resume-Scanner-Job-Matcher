package com.resumescanner.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Management Service Routes - Authentication
                .route("user-auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("lb://user-management-service"))

                // User Management Service Routes - User operations
                .route("user-management-service", r -> r
                        .path("/api/users/**")
                        .uri("lb://user-management-service"))

                // Resume Parser Service Routes
                .route("resume-parser-service", r -> r
                        .path("/api/resumes/**")
                        .uri("lb://resume-parser-service"))

                // Job Matcher Service Routes
                .route("job-matcher-service", r -> r
                        .path("/api/jobs/**", "/api/matches/**")
                        .uri("lb://job-matcher-service"))

                // Service Discovery Dashboard Route
                .route("service-discovery", r -> r
                        .path("/eureka/**")
                        .uri("http://localhost:8761"))

                // Swagger UI Routes for each service
                .route("user-management-swagger", r -> r
                        .path("/user-service/swagger-ui/**", "/user-service/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-management-service"))

                .route("resume-parser-swagger", r -> r
                        .path("/resume-service/swagger-ui/**", "/resume-service/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://resume-parser-service"))

                .route("job-matcher-swagger", r -> r
                        .path("/job-service/swagger-ui/**", "/job-service/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://job-matcher-service"))

                // Health check routes
                .route("user-management-health", r -> r
                        .path("/user-service/actuator/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-management-service"))

                .route("resume-parser-health", r -> r
                        .path("/resume-service/actuator/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://resume-parser-service"))

                .route("job-matcher-health", r -> r
                        .path("/job-service/actuator/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://job-matcher-service"))

                .build();
    }
}
