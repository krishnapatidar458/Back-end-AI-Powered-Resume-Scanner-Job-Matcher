package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeUploadRequest {
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private String fileName;
    private String fileType;
    private Long fileSize;
    private Boolean parseImmediately = true;
}
