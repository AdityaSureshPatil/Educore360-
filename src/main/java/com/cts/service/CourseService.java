package com.cts.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.CourseMaterialFileOutputDTO;
import com.cts.dto.CourseOutputDTO;

public interface CourseService {

    List<CourseOutputDTO> getAssignedCourses(Long instructorId);

    // Each upload creates a new row in course_material_file table
    CourseMaterialFileOutputDTO publishCourseMaterial(Long instructorId, Long courseId,
                                                       MultipartFile file,
                                                       String textContent);

    // Get all uploaded material files for a course
    List<CourseMaterialFileOutputDTO> getCourseMaterialFiles(Long courseId);
}