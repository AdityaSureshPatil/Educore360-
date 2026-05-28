package com.cts.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentOutputDTO {

    private Long studentId;
    private String enrollmentNumber;
    private LocalDate dateOfBirth;
    private String educationLevel;
    private String fieldOfInterest;
    private String country;
    private String bio;
    private String emergencyContact;
    private String addressLine;
    private String postalCode;
    private String status;

    // From linked User
    private Long userId;
    private String name;
    private String email;
    private String role;
}