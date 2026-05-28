package com.cts.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInputDTO {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    @Past(message = "Date of birth must be a past date")
    private LocalDate dateOfBirth;

    @Size(min = 2, max = 100, message = "Education level must be between 2 and 100 characters")
    private String educationLevel;

    @Size(min = 2, max = 150, message = "Field of interest must be between 2 and 150 characters")
    private String fieldOfInterest;

    @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
    private String country;

    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;

    @Pattern(regexp = "^[6-9]\\d{9}$",
             message = "Emergency contact must be a valid 10-digit mobile number")
    private String emergencyContact;

    @Size(max = 255, message = "Address line cannot exceed 255 characters")
    private String addressLine;

    @Pattern(regexp = "^[1-9][0-9]{5}$",
             message = "Postal code must be a valid 6-digit PIN code")
    private String postalCode;
}