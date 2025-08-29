package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemHealthDTO {
    private String serviceName;
    private String status; // UP, DOWN, DEGRADED
    private LocalDateTime timestamp;
    private String version;
    private List<ComponentHealth> components;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentHealth {
        private String name;
        private String status;
        private String details;
    }
}
