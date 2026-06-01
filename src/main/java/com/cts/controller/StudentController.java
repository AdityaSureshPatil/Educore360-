package com.cts.controller;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.*;
import com.cts.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    // 1. UPDATE STUDENT PROFILE
    @PutMapping("/profile/update")
    public ResponseEntity<StudentOutputDTO> updateStudentProfile(
            @Valid @RequestBody StudentInputDTO inputDTO) {
        return new ResponseEntity<>(
                studentService.updateStudentProfile(inputDTO), HttpStatus.OK);
    }

    // 2. GET STUDENT BY ID
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentOutputDTO> getStudentById(
            @PathVariable Long studentId) {
        return new ResponseEntity<>(
                studentService.getStudentById(studentId), HttpStatus.OK);
    }

    // 3. VIEW ALL COURSES (browse before enrolling)
    // GET /student/courses/all
    @GetMapping("/courses/all")
    public ResponseEntity<List<RegistrarCourseResponseDTO>> getAllCourses() {
        return new ResponseEntity<>(
                studentService.getAllCourses(), HttpStatus.OK);
    }

    // 4. SELF ENROLL IN A COURSE
    // POST /student/{studentId}/course/{courseId}/enroll
    @PostMapping("/{studentId}/course/{courseId}/enroll")
    public ResponseEntity<EnrollmentOutputDTO> enrollInCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return new ResponseEntity<>(
                studentService.enrollInCourse(studentId, courseId), HttpStatus.CREATED);
    }

    // 5. VIEW MY ENROLLED COURSES
    @GetMapping("/{studentId}/my-courses")
    public ResponseEntity<List<EnrollmentOutputDTO>> getMyEnrolledCourses(
            @PathVariable Long studentId) {
        return new ResponseEntity<>(
                studentService.getMyEnrolledCourses(studentId), HttpStatus.OK);
    }

    // 6. VIEW ASSIGNMENTS FOR AN ENROLLED COURSE
    @GetMapping("/{studentId}/course/{courseId}/assignments")
    public ResponseEntity<List<AssignmentOutputDTO>> getAssignmentsForCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return new ResponseEntity<>(
                studentService.getAssignmentsForCourse(studentId, courseId),
                HttpStatus.OK);
    }

    // 7. VIEW ALL COURSE MATERIAL FILES (enrolled only)
    // GET /student/{studentId}/course/{courseId}/materials
    @GetMapping("/{studentId}/course/{courseId}/materials")
    public ResponseEntity<List<CourseMaterialFileOutputDTO>> getCourseMaterialFiles(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return new ResponseEntity<>(
                studentService.getCourseMaterialFiles(studentId, courseId),
                HttpStatus.OK);
    }

    // 8. DOWNLOAD SPECIFIC COURSE MATERIAL FILE BY fileId (enrolled only)
    // GET /student/{studentId}/material/{fileId}/download
    @GetMapping("/{studentId}/material/{fileId}/download")
    public ResponseEntity<byte[]> downloadCourseMaterialFile(
            @PathVariable Long studentId,
            @PathVariable Long fileId) {
        byte[] bytes = studentService.downloadCourseMaterialFile(studentId, fileId);
        String fileName = studentService.getCourseMaterialFileName(studentId, fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(bytes);
    }

    // 9. VIEW ALL FILES FOR AN ASSIGNMENT (enrolled only)
    // GET /student/{studentId}/assignment/{assignmentId}/files
    @GetMapping("/{studentId}/assignment/{assignmentId}/files")
    public ResponseEntity<List<AssignmentFileOutputDTO>> getAssignmentFiles(
            @PathVariable Long studentId,
            @PathVariable Long assignmentId) {
        return new ResponseEntity<>(
                studentService.getAssignmentFiles(studentId, assignmentId),
                HttpStatus.OK);
    }

    // 10. DOWNLOAD SPECIFIC ASSIGNMENT FILE BY fileId (enrolled only)
    // GET /student/{studentId}/assignment-file/{fileId}/download
    @GetMapping("/{studentId}/assignment-file/{fileId}/download")
    public ResponseEntity<byte[]> downloadAssignmentFile(
            @PathVariable Long studentId,
            @PathVariable Long fileId) {
        byte[] bytes = studentService.downloadAssignmentFile(studentId, fileId);
        String fileName = studentService.getAssignmentFileName(studentId, fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(bytes);
    }

    // 11. SUBMIT ASSIGNMENT PDF (enrolled only)
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

    // 12. VIEW MY SUBMISSIONS
    @GetMapping("/{studentId}/my-submissions")
    public ResponseEntity<List<SubmissionOutputDTO>> getMySubmissions(
            @PathVariable Long studentId) {
        return new ResponseEntity<>(
                studentService.getMySubmissions(studentId), HttpStatus.OK);
    }
}