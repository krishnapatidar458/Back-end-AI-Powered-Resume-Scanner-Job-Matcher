package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type; // INFO, SUCCESS, WARNING, ERROR
    private Boolean isRead;
    private String actionUrl;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
