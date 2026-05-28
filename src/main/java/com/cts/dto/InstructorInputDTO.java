package com.cts.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorInputDTO {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    @NotBlank(message = "Skill is required")
    @Size(min = 2, max = 100, message = "Skill must be between 2 and 100 characters")
    private String skill;

    @NotNull(message = "Experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 50, message = "Experience cannot exceed 50 years")
    private Integer experience;

    @Pattern(regexp = "^[6-9]\\d{9}$",
             message = "Emergency contact must be a valid 10-digit mobile number")
    private String emergencyContact;

    @Size(max = 255, message = "Address line cannot exceed 255 characters")
    private String addressLine;

    @Pattern(regexp = "^[1-9][0-9]{5}$",
             message = "Postal code must be a valid 6-digit PIN code")
    private String postalCode;
}