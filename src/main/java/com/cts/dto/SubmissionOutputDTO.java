package com.cts.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionOutputDTO {

    private Long submissionId;

    // Student info (needed by instructor to identify who submitted)
    private Long studentId;
    private String studentName;
    private String enrollmentNumber;

    // File info — original filename only, never expose server filePath
    private String fileName;
    private String textContent;

    private LocalDateTime submittedAt;

    // Grading — null until instructor grades
    private Double grade;
    private String feedback;
    private LocalDateTime gradedAt;

    // SUBMITTED / GRADED / LATE
    private String status;

    // Assignment info
    private Long assignmentId;
    private String assignmentTitle;

    // Course info
    private Long courseId;
    private String courseTitle;
}