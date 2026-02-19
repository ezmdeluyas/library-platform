package com.zmd.auth_service.dto.request;

import jakarta.validation.constraints.*;

public record RegisterRequest(

        @Email(message = "Invalid email")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters long")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,16}$",
                 message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
        String password,

        @Size(max = 255)
        @NotBlank(message = "First name is required")
        String firstName,

        @Size(max = 255)
        @NotBlank(message = "Last name is required")
        String lastName
) {}
