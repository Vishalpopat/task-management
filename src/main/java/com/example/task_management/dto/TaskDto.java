package com.example.task_management.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
}
