package com.cts.service;

import com.cts.dto.EnrollmentOutputDTO;
import com.cts.dto.RegistrarCourseCreateDTO;
import com.cts.dto.RegistrarCourseResponseDTO;
import java.util.List;

public interface RegistrarAcademicService {

    // Creates course AND assigns instructor in one step
    RegistrarCourseResponseDTO provisionNewCourse(RegistrarCourseCreateDTO createDTO);

    List<RegistrarCourseResponseDTO> getAllConfiguredCourses();

    List<EnrollmentOutputDTO> getEnrolledStudents(Long courseId);
}