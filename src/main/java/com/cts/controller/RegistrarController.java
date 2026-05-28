package com.cts.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cts.dto.EnrollmentOutputDTO;
import com.cts.dto.RegistrarCourseCreateDTO;
import com.cts.dto.RegistrarCourseResponseDTO;
import com.cts.dto.RegistrarInputDTO;
import com.cts.dto.RegistrarOutputDTO;
import com.cts.service.RegistrarAcademicService;
import com.cts.service.RegistrarProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/academic")
public class RegistrarController {

    private final RegistrarAcademicService academicService;
    private final RegistrarProfileService profileService;

    // 1. UPDATE REGISTRAR PROFILE
    // PUT /api/academic/registrar/profile/update
    @PutMapping("/registrar/profile/update")
    public ResponseEntity<RegistrarOutputDTO> updateRegistrarProfile(
            @Valid @RequestBody RegistrarInputDTO inputDTO) {
        RegistrarOutputDTO response = profileService.updateRegistrarProfile(inputDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 2. CREATE A NEW COURSE
    // POST /api/academic/registrar/course
    @PostMapping("/registrar/course")
    public ResponseEntity<RegistrarCourseResponseDTO> provisionNewCourse(
            @Valid @RequestBody RegistrarCourseCreateDTO createDTO) {
        RegistrarCourseResponseDTO response = academicService.provisionNewCourse(createDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 3. ASSIGN INSTRUCTOR TO COURSE
    // PUT /api/academic/course/{courseId}/assign-instructor/{instructorId}
    @PutMapping("/course/{courseId}/assign-instructor/{instructorId}")
    public ResponseEntity<RegistrarCourseResponseDTO> assignInstructorToCourse(
            @PathVariable Long courseId,
            @PathVariable Long instructorId) {
        RegistrarCourseResponseDTO response = academicService
                .assignInstructorToCourse(courseId, instructorId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 4. VIEW ALL COURSES
    // GET /api/academic/registrar/courses
    @GetMapping("/registrar/courses")
    public ResponseEntity<List<RegistrarCourseResponseDTO>> getAllConfiguredCourses() {
        List<RegistrarCourseResponseDTO> courses = academicService.getAllConfiguredCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    // 5. ENROLL STUDENT IN A COURSE
    // POST /api/academic/course/{courseId}/enroll/{studentId}
    @PostMapping("/course/{courseId}/enroll/{studentId}")
    public ResponseEntity<EnrollmentOutputDTO> enrollStudentInCourse(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        EnrollmentOutputDTO response = academicService
                .enrollStudentInCourse(courseId, studentId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 6. VIEW ALL ENROLLED STUDENTS FOR A COURSE
    // GET /api/academic/course/{courseId}/enrolled-students
    @GetMapping("/course/{courseId}/enrolled-students")
    public ResponseEntity<List<EnrollmentOutputDTO>> getEnrolledStudents(
            @PathVariable Long courseId) {
        List<EnrollmentOutputDTO> enrollments = academicService
                .getEnrolledStudents(courseId);
        return new ResponseEntity<>(enrollments, HttpStatus.OK);
    }
}