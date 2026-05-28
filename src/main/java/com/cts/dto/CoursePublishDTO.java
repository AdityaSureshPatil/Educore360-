package com.cts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CoursePublishDTO {

    private String learningMaterial;
    @NotBlank(message = "Assignment instructions cannot be empty")
    @Size(min = 10, message = "Assignment instructions must be at least 10 characters long")
    private String assignmentInstructions;
}