package com.cts.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOutputDTO {

    private Long courseId;
    private String title;
    private String description;
    private String targetSkill;
    private Integer credits;
    private String syllabusPath;
    private String version;

    // DRAFT / PENDING_APPROVAL / PUBLISHED
    private String status;

    // Text material published by instructor (null if PDF-only)
    private String learningMaterial;

    private boolean isPublished;

    // Assigned instructor info
    private Long instructorId;
    private String instructorName;
}