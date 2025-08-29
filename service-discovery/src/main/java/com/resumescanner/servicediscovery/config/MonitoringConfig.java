package com.resumescanner.servicediscovery.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MonitoringConfig {

    @Autowired
    private Environment environment;

    @Bean
    public InfoContributor serviceDiscoveryInfoContributor() {
        return new InfoContributor() {
            @Override
            public void contribute(Info.Builder builder) {
                Map<String, Object> serviceInfo = new HashMap<>();
                serviceInfo.put("name", "AI-Powered Resume Scanner - Service Discovery");
                serviceInfo.put("description", "Eureka Server for microservices registration and discovery");
                serviceInfo.put("version", "1.0.0");
                serviceInfo.put("startup-time", LocalDateTime.now().toString());
                serviceInfo.put("active-profile", environment.getActiveProfiles());
                serviceInfo.put("eureka-port", environment.getProperty("server.port", "8761"));
                serviceInfo.put("self-preservation", environment.getProperty("eureka.server.enable-self-preservation", "true"));
                
                builder.withDetail("service-discovery", serviceInfo);
            }
        };
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configureMetrics() {
        return registry -> registry.config().commonTags(
            Tags.of(
                "service", "service-discovery",
                "application", "ai-resume-scanner",
                "version", "1.0.0"
            )
        );
    }
}
