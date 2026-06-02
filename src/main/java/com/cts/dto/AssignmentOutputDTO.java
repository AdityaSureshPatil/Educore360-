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
    private Double totalMarks;
    private LocalDateTime publishedAt;

    // Course info
    private Long courseId;
    private String courseTitle;
}