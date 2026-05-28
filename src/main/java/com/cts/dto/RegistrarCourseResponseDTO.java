package com.cts.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarCourseResponseDTO {

    private Long courseId;
    private String title;
    private Integer credits;
    private String syllabusPath;
    private String version;
    private String status;
    private Long instructorId;
    private String instructorName;
}