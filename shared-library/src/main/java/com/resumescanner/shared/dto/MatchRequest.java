package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequest {
    @NotNull(message = "Resume ID is required")
    private Long resumeId;
    
    @NotNull(message = "Job ID is required")
    private Long jobId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
}
