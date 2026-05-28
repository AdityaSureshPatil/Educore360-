package com.cts.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentInputDTO {

    @NotNull(message = "Course ID is required")
    @Positive(message = "Course ID must be a positive number")
    private Long courseId;

    @NotBlank(message = "Assignment title is required")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    private String title;

    @Size(max = 2000, message = "Instructions cannot exceed 2000 characters")
    private String instructions;

    @NotNull(message = "Total marks is required")
    @DecimalMin(value = "1.0", message = "Total marks must be at least 1")
    @DecimalMax(value = "100.0", message = "Total marks cannot exceed 100")
    private Double totalMarks;
}