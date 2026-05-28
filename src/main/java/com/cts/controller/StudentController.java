package com.cts.controller;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.AssignmentOutputDTO;
import com.cts.dto.EnrollmentOutputDTO;
import com.cts.dto.StudentInputDTO;
import com.cts.dto.StudentOutputDTO;
import com.cts.dto.SubmissionOutputDTO;
import com.cts.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    // 1. UPDATE STUDENT PROFILE
    // PUT /student/profile/update
    @PutMapping("/profile/update")
    public ResponseEntity<StudentOutputDTO> updateStudentProfile(
            @Valid @RequestBody StudentInputDTO inputDTO) {
        return new ResponseEntity<>(
                studentService.updateStudentProfile(inputDTO), HttpStatus.OK);
    }

    // 2. GET STUDENT BY ID
    // GET /student/{studentId}
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentOutputDTO> getStudentById(
            @PathVariable Long studentId) {
        return new ResponseEntity<>(
                studentService.getStudentById(studentId), HttpStatus.OK);
    }

    // 3. SELF ENROLL IN A COURSE
    // POST /student/{studentId}/course/{courseId}/enroll
    @PostMapping("/{studentId}/course/{courseId}/enroll")
    public ResponseEntity<EnrollmentOutputDTO> enrollInCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return new ResponseEntity<>(
                studentService.enrollInCourse(studentId, courseId), HttpStatus.CREATED);
    }

    // 4. VIEW MY ENROLLED COURSES
    // GET /student/{studentId}/my-courses
    @GetMapping("/{studentId}/my-courses")
    public ResponseEntity<List<EnrollmentOutputDTO>> getMyEnrolledCourses(
            @PathVariable Long studentId) {
        return new ResponseEntity<>(
                studentService.getMyEnrolledCourses(studentId), HttpStatus.OK);
    }

    // 5. VIEW ASSIGNMENTS FOR AN ENROLLED COURSE
    // GET /student/{studentId}/course/{courseId}/assignments
    @GetMapping("/{studentId}/course/{courseId}/assignments")
    public ResponseEntity<List<AssignmentOutputDTO>> getAssignmentsForCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return new ResponseEntity<>(
                studentService.getAssignmentsForCourse(studentId, courseId),
                HttpStatus.OK);
    }

    // 6. DOWNLOAD COURSE MATERIAL PDF (enrolled students only)
    // GET /student/{studentId}/course/{courseId}/material/download
    @GetMapping("/{studentId}/course/{courseId}/material/download")
    public ResponseEntity<byte[]> downloadCourseMaterial(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        byte[] fileBytes = studentService.downloadCourseMaterial(studentId, courseId);
        String fileName = studentService.getCourseMaterialFileName(studentId, courseId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(fileBytes);
    }

    // 7. DOWNLOAD ASSIGNMENT PDF (enrolled students only)
    // GET /student/{studentId}/assignment/{assignmentId}/download
    @GetMapping("/{studentId}/assignment/{assignmentId}/download")
    public ResponseEntity<byte[]> downloadAssignmentFile(
            @PathVariable Long studentId,
            @PathVariable Long assignmentId) {
        byte[] fileBytes = studentService.downloadAssignmentFile(studentId, assignmentId);
        String fileName = studentService.getAssignmentFileName(studentId, assignmentId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(fileBytes);
    }

    // 8. SUBMIT ASSIGNMENT PDF (enrolled students only)
    // POST /student/{studentId}/assignment/{assignmentId}/submit
    @PostMapping(value = "/{studentId}/assignment/{assignmentId}/submit",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionOutputDTO> submitAssignment(
            @PathVariable Long studentId,
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(
                studentService.submitAssignment(studentId, assignmentId, file),
                HttpStatus.CREATED);
    }

    // 9. VIEW MY SUBMISSIONS
    // GET /student/{studentId}/my-submissions
    @GetMapping("/{studentId}/my-submissions")
    public ResponseEntity<List<SubmissionOutputDTO>> getMySubmissions(
            @PathVariable Long studentId) {
        return new ResponseEntity<>(
                studentService.getMySubmissions(studentId), HttpStatus.OK);
    }
}