package com.cts.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorOutputDTO {

    private Long instructorId;
    private String skill;
    private Integer experience;
    private LocalDate dateOfBirth;
    private String status;
    private String emergencyContact;
    private String addressLine;
    private String postalCode;

    // From linked User
    private Long userId;
    private String name;
    private String email;
    private String role;
}