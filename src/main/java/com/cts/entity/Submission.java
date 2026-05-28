package com.cts.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "submission")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long submissionId;

    // WHO submitted — FK → student table (CRITICAL: was missing before)
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "assignment_id", referencedColumnName = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false)
    private Course course;

    // Student submits either a file OR text
    @Column(name = "file_path")
    private String filePath;

    // Original filename shown to instructor (never expose filePath to client)
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "text_content", columnDefinition = "TEXT")
    private String textContent;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    // Filled by instructor after grading
    @Column(name = "grade")
    private Double grade;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    // SUBMITTED / GRADED / LATE
    @Column(name = "status", nullable = false)
    private String status;
}