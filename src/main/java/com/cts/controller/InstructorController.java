package com.cts.controller;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.*;
import com.cts.service.AssignmentService;
import com.cts.service.CourseService;
import com.cts.service.InstructorService;
import com.cts.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/instructor")
public class InstructorController {

    private final InstructorService instructorService;
    private final CourseService courseService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    // 1. UPDATE INSTRUCTOR PROFILE
    @PutMapping("/profile/update")
    public ResponseEntity<InstructorOutputDTO> updateInstructorProfile(
            @Valid @RequestBody InstructorInputDTO inputDTO) {
        return new ResponseEntity<>(
                instructorService.updateInstructorProfile(inputDTO), HttpStatus.OK);
    }

    // 2. VIEW ASSIGNED COURSES
    @GetMapping("/my-courses/{instructorId}")
    public ResponseEntity<List<CourseOutputDTO>> getAssignedCourses(
            @PathVariable Long instructorId) {
        return new ResponseEntity<>(
                courseService.getAssignedCourses(instructorId), HttpStatus.OK);
    }

    // 3. PUBLISH COURSE MATERIAL (PDF or Text)
    // Each upload creates a new row in course_material_file table
    @PostMapping(value = "/course/{courseId}/material",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseMaterialFileOutputDTO> publishCourseMaterial(
            @PathVariable Long courseId,
            @RequestParam Long instructorId,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String textContent) {
        return new ResponseEntity<>(
                courseService.publishCourseMaterial(instructorId, courseId,
                        file, textContent),
                HttpStatus.OK);
    }

    // 4. VIEW ALL MATERIAL FILES FOR A COURSE
    // GET /instructor/course/{courseId}/materials
    @GetMapping("/course/{courseId}/materials")
    public ResponseEntity<List<CourseMaterialFileOutputDTO>> getCourseMaterials(
            @PathVariable Long courseId) {
        return new ResponseEntity<>(
                courseService.getCourseMaterialFiles(courseId), HttpStatus.OK);
    }

    // 5. PUBLISH ASSIGNMENT (PDF or Text)
    // Each publish creates a new Assignment + new AssignmentFile row
    @PostMapping(value = "/assignment/publish",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssignmentOutputDTO> publishAssignment(
            @RequestParam Long instructorId,
            @RequestParam String title,
            @RequestParam Long courseId,
            @RequestParam(required = false) String instructions,
            @RequestParam(required = false) Double totalMarks,
            @RequestParam(required = false) MultipartFile file) {
        AssignmentInputDTO inputDTO = AssignmentInputDTO.builder()
                .courseId(courseId).title(title)
                .instructions(instructions).totalMarks(totalMarks).build();
        return new ResponseEntity<>(
                assignmentService.publishAssignment(instructorId, inputDTO, file),
                HttpStatus.CREATED);
    }

    // 6. VIEW ALL FILES FOR AN ASSIGNMENT
    // GET /instructor/assignment/{assignmentId}/files
    @GetMapping("/assignment/{assignmentId}/files")
    public ResponseEntity<List<AssignmentFileOutputDTO>> getAssignmentFiles(
            @PathVariable Long assignmentId) {
        return new ResponseEntity<>(
                assignmentService.getAssignmentFiles(assignmentId), HttpStatus.OK);
    }

    // 7. VIEW STUDENT SUBMISSIONS FOR A COURSE
    @GetMapping("/course/{courseId}/submissions")
    public ResponseEntity<List<SubmissionOutputDTO>> getSubmissions(
            @PathVariable Long courseId,
            @RequestParam Long instructorId) {
        return new ResponseEntity<>(
                submissionService.getSubmissionsForCourse(instructorId, courseId),
                HttpStatus.OK);
    }

    // 8. DOWNLOAD STUDENT SUBMISSION PDF
    @GetMapping("/submission/{submissionId}/download")
    public ResponseEntity<byte[]> downloadSubmissionFile(
            @PathVariable Long submissionId,
            @RequestParam Long instructorId) {
        byte[] fileBytes = submissionService.downloadSubmissionFile(
                instructorId, submissionId);
        String fileName = submissionService.getSubmissionFileName(
                instructorId, submissionId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(fileBytes);
    }

    // 9. GRADE A SUBMISSION
    @PutMapping("/submission/{submissionId}/grade")
    public ResponseEntity<SubmissionOutputDTO> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam Long instructorId,
            @Valid @RequestBody GradeInputDTO gradeInputDTO) {
        return new ResponseEntity<>(
                submissionService.gradeSubmission(instructorId, submissionId,
                        gradeInputDTO),
                HttpStatus.OK);
    }
}