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
        return new ResponseEntity<>(
                profileService.updateRegistrarProfile(inputDTO), HttpStatus.OK);
    }

    // 2. CREATE COURSE + ASSIGN INSTRUCTOR (combined)
    // POST /api/academic/registrar/course
    @PostMapping("/registrar/course")
    public ResponseEntity<RegistrarCourseResponseDTO> provisionNewCourse(
            @Valid @RequestBody RegistrarCourseCreateDTO createDTO) {
        return new ResponseEntity<>(
                academicService.provisionNewCourse(createDTO), HttpStatus.CREATED);
    }

    // 3. VIEW ALL COURSES
    // GET /api/academic/registrar/courses
    @GetMapping("/registrar/courses")
    public ResponseEntity<List<RegistrarCourseResponseDTO>> getAllConfiguredCourses() {
        return new ResponseEntity<>(
                academicService.getAllConfiguredCourses(), HttpStatus.OK);
    }

    // 4. VIEW ALL ENROLLED STUDENTS FOR A COURSE
    // GET /api/academic/course/{courseId}/enrolled-students
    @GetMapping("/course/{courseId}/enrolled-students")
    public ResponseEntity<List<EnrollmentOutputDTO>> getEnrolledStudents(
            @PathVariable Long courseId) {
        return new ResponseEntity<>(
                academicService.getEnrolledStudents(courseId), HttpStatus.OK);
    }
}