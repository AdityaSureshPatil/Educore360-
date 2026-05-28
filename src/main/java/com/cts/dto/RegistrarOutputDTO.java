package com.cts.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarOutputDTO {

    private Long registrarId;
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