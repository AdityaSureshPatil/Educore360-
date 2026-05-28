package com.cts.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.CourseOutputDTO;

public interface CourseService {

    // Used by instructor
    List<CourseOutputDTO> getAssignedCourses(Long instructorId);

    CourseOutputDTO publishCourseMaterial(Long instructorId, Long courseId,
                                          MultipartFile file, String textContent);
}