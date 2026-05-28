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

    // Core fields (set by registrar)
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
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

    // Assigned by registrar
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    // Set by instructor when publishing material
    @Lob
    @Column(name = "learning_material", columnDefinition = "TEXT")
    private String learningMaterial;

    // Path to uploaded PDF on local server (if material is a file)
    @Column(name = "material_file_path")
    private String materialFilePath;

    // Original filename shown to students
    @Column(name = "material_file_name")
    private String materialFileName;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished = false;
}