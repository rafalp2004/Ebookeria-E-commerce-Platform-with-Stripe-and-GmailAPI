package com.ebookeria.ecommerce.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDTO(
        @NotBlank
        @Size(min=2, max=50, message="First name must be between 2 and 50 characters")
        String firstName,

        @NotBlank
        @Size(min=2, max=50, message="Last name must be between 2 and 50 characters")
        String lastName,

        @Email
        String email,

        @NotBlank
        @Size(min=2, max=50, message="Password must be between 2 and 50 characters")
        String password
) {
}