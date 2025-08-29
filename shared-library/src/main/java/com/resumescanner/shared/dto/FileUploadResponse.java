package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String uploadStatus;
    private String message;
    private LocalDateTime uploadedAt;
}
