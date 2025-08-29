package com.resumescanner.servicediscovery.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("customEurekaHealthIndicator")
@Primary
public class CustomEurekaHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Add custom health checks here
        // For example, check if Eureka server is responding
        try {
            // Simple check - if we can create this health indicator, service is up
            return Health.up()
                    .withDetail("eureka.server", "UP")
                    .withDetail("status", "Service Discovery is running")
                    .withDetail("description", "Eureka Server for microservices registration and discovery")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("eureka.server", "DOWN")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
