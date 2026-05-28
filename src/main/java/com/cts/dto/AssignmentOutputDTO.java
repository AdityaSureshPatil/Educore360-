package com.cts.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentOutputDTO {

    private Long assignmentId;
    private String title;
    private String instructions;

    // Original filename shown to students (never expose filePath)
    private String fileName;

    private Double totalMarks;
    private LocalDateTime publishedAt;

    // Course info
    private Long courseId;
    private String courseTitle;
}