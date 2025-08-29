package com.resumescanner.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {
    @Email(message = "Email should be valid")
    private String email;
    
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String currentPassword;
    private String newPassword;
}
