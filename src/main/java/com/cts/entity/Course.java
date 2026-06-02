package com.cts.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "courses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_skill")
    private String targetSkill;

    @Column(name = "credits")
    private Integer credits;

    @Column(name = "syllabus_path")
    private String syllabusPath;

    @Column(name = "version")
    private String version;

    // DRAFT / PENDING_APPROVAL / PUBLISHED
    @Column(name = "status")
    private String status;

    // Text-based learning material (optional — PDF files stored in course_material_file)
    @Column(name = "learning_material", columnDefinition = "TEXT")
    private String learningMaterial;

    @Column(name = "is_published")
    private boolean isPublished;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;
}