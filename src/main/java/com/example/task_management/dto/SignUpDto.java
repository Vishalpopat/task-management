package com.example.task_management.dto;

import com.example.task_management.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUpDto {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
    private User.Role role = User.Role.USER;
}