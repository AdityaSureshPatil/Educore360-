package com.cts.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrarCourseCreateDTO {

    @NotBlank(message = "Course title cannot be blank")
    @Size(min = 3, max = 100, message = "Course title must be between 3 and 100 characters")
    private String title;

    @NotNull(message = "Credits value is required")
    @Min(value = 1, message = "Course must carry at least 1 credit")
    @Max(value = 6, message = "Course cannot carry more than 6 credits")
    private Integer credits;

    @NotBlank(message = "Syllabus file path is required")
    private String syllabusPath;

    @NotBlank(message = "Course framework version string is required")
    private String version;

    // Instructor assigned at course creation time
    @NotNull(message = "Instructor ID is required")
    @Positive(message = "Instructor ID must be a positive number")
    private Long instructorId;
}