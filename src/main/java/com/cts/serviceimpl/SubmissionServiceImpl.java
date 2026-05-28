package com.cts.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.cts.dto.GradeInputDTO;
import com.cts.dto.SubmissionOutputDTO;
import com.cts.entity.Course;
import com.cts.entity.Submission;
import com.cts.exception.CourseNotAssignedToInstructorException;
import com.cts.exception.CourseNotFoundException;
import com.cts.exception.InstructorNotFoundException;
import com.cts.exception.InvalidFileException;
import com.cts.exception.InvalidGradeException;
import com.cts.exception.SubmissionNotFoundException;
import com.cts.repository.CourseRepository;
import com.cts.repository.InstructorRepository;
import com.cts.repository.SubmissionRepository;
import com.cts.service.FileStorageService;
import com.cts.service.SubmissionService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final FileStorageService fileStorageService;

    // ── VIEW SUBMISSIONS FOR A COURSE ─────────────────────────────────

    @Override
    public List<SubmissionOutputDTO> getSubmissionsForCourse(Long instructorId,
                                                              Long courseId) {
        verifyOwnership(instructorId, courseId);

        List<Submission> submissions = submissionRepository
                .findByCourse_CourseId(courseId);
        if (submissions.isEmpty()) {
            throw new SubmissionNotFoundException(
                    "No submissions found for course id: " + courseId);
        }
        return submissions.stream()
                .map(this::mapToOutputDTO)
                .collect(Collectors.toList());
    }

    // ── DOWNLOAD STUDENT SUBMISSION PDF ───────────────────────────────

    @Override
    public byte[] downloadSubmissionFile(Long instructorId, Long submissionId) {

        Submission submission = getVerifiedSubmission(instructorId, submissionId);

        if (submission.getFilePath() == null || submission.getFilePath().isBlank()) {
            throw new InvalidFileException(
                    "No PDF file found for submission id: " + submissionId
                    + ". Student submitted text only.");
        }

        return fileStorageService.loadFile(submission.getFilePath());
    }

    @Override
    public String getSubmissionFileName(Long instructorId, Long submissionId) {
        return getVerifiedSubmission(instructorId, submissionId).getFileName();
    }

    // ── GRADE A SUBMISSION ────────────────────────────────────────────

    @Override
    public SubmissionOutputDTO gradeSubmission(Long instructorId, Long submissionId,
                                                GradeInputDTO gradeInputDTO) {

        if (gradeInputDTO.getGrade() == null
                || gradeInputDTO.getGrade() < 0.0
                || gradeInputDTO.getGrade() > 100.0) {
            throw new InvalidGradeException("Grade must be between 0.0 and 100.0");
        }
        //check -2
       

        Submission submission = getVerifiedSubmission(instructorId, submissionId);

        submission.setGrade(gradeInputDTO.getGrade());
        submission.setFeedback(gradeInputDTO.getFeedback());
        submission.setGradedAt(LocalDateTime.now());
        submission.setStatus("GRADED");

        return mapToOutputDTO(submissionRepository.save(submission));
    }

    // ── Helpers ───────────────────────────────────────────────────────

    // Fetch submission and verify it belongs to this instructor's course
    private Submission getVerifiedSubmission(Long instructorId, Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new SubmissionNotFoundException(
                        "Submission not found with id: " + submissionId));
        verifyOwnership(instructorId, submission.getCourse().getCourseId());
        return submission;
    }

    private void verifyOwnership(Long instructorId, Long courseId) {
        if (!instructorRepository.existsById(instructorId)) {
            throw new InstructorNotFoundException(
                    "Instructor not found with id: " + instructorId);
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course not found with id: " + courseId));
        if (course.getInstructor() == null
                || !course.getInstructor().getInstructorId().equals(instructorId)) {
            throw new CourseNotAssignedToInstructorException(
                    "Course " + courseId + " is not assigned to instructor " + instructorId);
        }
    }

    private SubmissionOutputDTO mapToOutputDTO(Submission s) {
        return SubmissionOutputDTO.builder()
                .submissionId(s.getSubmissionId())
                .studentId(s.getStudent().getStudentId())
                .studentName(s.getStudent().getUser().getName())
                .enrollmentNumber(s.getStudent().getEnrollmentNumber())
                .fileName(s.getFileName())
                .textContent(s.getTextContent())
                .submittedAt(s.getSubmittedAt())
                .grade(s.getGrade())
                .feedback(s.getFeedback())
                .gradedAt(s.getGradedAt())
                .status(s.getStatus())
                .assignmentId(s.getAssignment().getAssignmentId())
                .assignmentTitle(s.getAssignment().getTitle())
                .courseId(s.getCourse().getCourseId())
                .courseTitle(s.getCourse().getTitle())
                .build();
    }
}