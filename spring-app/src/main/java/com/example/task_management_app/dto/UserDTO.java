package com.example.task_management_app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer id;

    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username must be less than 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    private String password;

    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;
    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;

    private String role;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}