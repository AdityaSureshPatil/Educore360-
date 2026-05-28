package com.cts.controller;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.AssignmentOutputDTO;
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
        StudentOutputDTO response = studentService.updateStudentProfile(inputDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 2. GET STUDENT BY ID
    // GET /student/{studentId}
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentOutputDTO> getStudentById(
            @PathVariable Long studentId) {
        StudentOutputDTO response = studentService.getStudentById(studentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 3. VIEW ALL ASSIGNMENTS FOR A COURSE (enrolled students only)
    // GET /student/{studentId}/course/{courseId}/assignments
    @GetMapping("/{studentId}/course/{courseId}/assignments")
    public ResponseEntity<List<AssignmentOutputDTO>> getAssignmentsForCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        List<AssignmentOutputDTO> assignments = studentService
                .getAssignmentsForCourse(studentId, courseId);
        return new ResponseEntity<>(assignments, HttpStatus.OK);
    }

    // 4. DOWNLOAD ASSIGNMENT PDF (enrolled students only)
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

    // 5. SUBMIT ASSIGNMENT PDF (enrolled students only)
    // POST /student/{studentId}/assignment/{assignmentId}/submit
    @PostMapping(value = "/{studentId}/assignment/{assignmentId}/submit",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionOutputDTO> submitAssignment(
            @PathVariable Long studentId,
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file) {
        SubmissionOutputDTO response = studentService.submitAssignment(
                studentId, assignmentId, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 6. VIEW MY SUBMISSIONS
    // GET /student/{studentId}/my-submissions
    @GetMapping("/{studentId}/my-submissions")
    public ResponseEntity<List<SubmissionOutputDTO>> getMySubmissions(
            @PathVariable Long studentId) {
        List<SubmissionOutputDTO> submissions = studentService
                .getMySubmissions(studentId);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }
}