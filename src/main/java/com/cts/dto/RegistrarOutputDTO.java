package com.cts.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarOutputDTO {

    private Long registrarId;
    private LocalDate dateOfBirth;
    private String emergencyContact;
    private String addressLine;
    private String postalCode;

    // Live count from courses table — not stored
    private Integer publishedCourseCount;

    // From linked User
    private Long userId;
    private String name;
    private String email;
    private String role;
    private String status;
}