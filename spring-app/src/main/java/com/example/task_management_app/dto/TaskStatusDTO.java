package com.example.task_management_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusDTO {

    private Integer id;

    @NotBlank(message = "Status name is required")
    @Size(max = 50, message = "Status name must be less than 50 characters")
    private String name;

    private String description;

    @Size(max = 20, message = "Color code must be less than 20 characters")
    private String color;
}