package com.example.task_management_app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Integer id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    private String description;

    private LocalDate dueDate;

    @Min(value = 1, message = "Priority must be between 1 and 5")
    @Max(value = 5, message = "Priority must be between 1 and 5")
    private Integer priority;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    private Integer userId;

    private String username;

    private Integer statusId;

    private String statusName;

    private Integer categoryId;

    private String categoryName;
}