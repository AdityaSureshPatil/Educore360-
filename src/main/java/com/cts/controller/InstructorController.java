package com.cts.controller;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.AssignmentInputDTO;
import com.cts.dto.AssignmentOutputDTO;
import com.cts.dto.CourseOutputDTO;
import com.cts.dto.GradeInputDTO;
import com.cts.dto.InstructorInputDTO;
import com.cts.dto.InstructorOutputDTO;
import com.cts.dto.SubmissionOutputDTO;
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
    // PUT /instructor/profile/update
    @PutMapping("/profile/update")
    public ResponseEntity<InstructorOutputDTO> updateInstructorProfile(
            @Valid @RequestBody InstructorInputDTO inputDTO) {
        InstructorOutputDTO response = instructorService.updateInstructorProfile(inputDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 2. VIEW ASSIGNED COURSES
    // GET /instructor/my-courses/{instructorId}
    @GetMapping("/my-courses/{instructorId}")
    public ResponseEntity<List<CourseOutputDTO>> getAssignedCourses(
            @PathVariable Long instructorId) {
        List<CourseOutputDTO> courses = courseService.getAssignedCourses(instructorId);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    // 3. PUBLISH COURSE MATERIAL (PDF or Text)
    // POST /instructor/course/{courseId}/material
    @PostMapping(value = "/course/{courseId}/material",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseOutputDTO> publishCourseMaterial(
            @PathVariable Long courseId,
            @RequestParam Long instructorId,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String textContent) {
        CourseOutputDTO response = courseService.publishCourseMaterial(
                instructorId, courseId, file, textContent);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 4. PUBLISH ASSIGNMENT (PDF or Text)
    // POST /instructor/assignment/publish
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
                .courseId(courseId)
                .title(title)
                .instructions(instructions)
                .totalMarks(totalMarks)
                .build();

        AssignmentOutputDTO response = assignmentService.publishAssignment(
                instructorId, inputDTO, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 5. VIEW STUDENT SUBMISSIONS FOR A COURSE
    // GET /instructor/course/{courseId}/submissions?instructorId=
    @GetMapping("/course/{courseId}/submissions")
    public ResponseEntity<List<SubmissionOutputDTO>> getSubmissions(
            @PathVariable Long courseId,
            @RequestParam Long instructorId) {
        List<SubmissionOutputDTO> submissions = submissionService
                .getSubmissionsForCourse(instructorId, courseId);
        return new ResponseEntity<>(submissions, HttpStatus.OK);
    }

    // 6. DOWNLOAD STUDENT SUBMISSION PDF
    // GET /instructor/submission/{submissionId}/download?instructorId=
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

    // 7. GRADE A SUBMISSION
    // PUT /instructor/submission/{submissionId}/grade?instructorId=
    @PutMapping("/submission/{submissionId}/grade")
    public ResponseEntity<SubmissionOutputDTO> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam Long instructorId,
            @Valid @RequestBody GradeInputDTO gradeInputDTO) {
        SubmissionOutputDTO response = submissionService.gradeSubmission(
                instructorId, submissionId, gradeInputDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}