package com.cts.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarInputDTO {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;
    
    @NotNull(message = "Instructor ID is required")
    @Positive(message = "Instructor ID should be positive")
    private long InstructorId;
    
    @Pattern(regexp = "^[6-9]\\d{9}$",
             message = "Emergency contact must be a valid 10-digit mobile number")
    private String emergencyContact;

    @Size(max = 255, message = "Address line cannot exceed 255 characters")
    private String addressLine;

    @Pattern(regexp = "^[1-9][0-9]{5}$",
             message = "Postal code must be a valid 6-digit PIN code")
    private String postalCode;
}