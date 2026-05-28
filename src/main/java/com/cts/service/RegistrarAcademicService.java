package com.cts.service;

import com.cts.dto.EnrollmentOutputDTO;
import com.cts.dto.RegistrarCourseCreateDTO;
import com.cts.dto.RegistrarCourseResponseDTO;
import java.util.List;

public interface RegistrarAcademicService {

    RegistrarCourseResponseDTO provisionNewCourse(RegistrarCourseCreateDTO createDTO);

    RegistrarCourseResponseDTO assignInstructorToCourse(Long courseId, Long instructorId);

    List<RegistrarCourseResponseDTO> getAllConfiguredCourses();

    // View all students enrolled in a course (read-only for registrar)
    List<EnrollmentOutputDTO> getEnrolledStudents(Long courseId);
}