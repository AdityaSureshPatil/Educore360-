package com.cts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationInputDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(
        regexp = "STUDENT|INSTRUCTOR|REGISTRAR|EXAM_COORDINATOR|FINANCE_OFFICER",
        message = "Invalid Role. Must be one of: STUDENT, INSTRUCTOR, REGISTRAR, EXAM_COORDINATOR, FINANCE_OFFICER"
    )
    private String role;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    @Schema(example="string")
    private String phone;
}