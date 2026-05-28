package com.cts.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.AssignmentOutputDTO;
import com.cts.dto.StudentInputDTO;
import com.cts.dto.StudentOutputDTO;
import com.cts.dto.SubmissionOutputDTO;
import com.cts.entity.Assignment;
import com.cts.entity.Student;
import com.cts.entity.Submission;
import com.cts.exception.AssignmentNotFoundException;
import com.cts.exception.InvalidFileException;
import com.cts.exception.NotEnrolledException;
import com.cts.exception.StudentNotFoundException;
import com.cts.exception.SubmissionNotFoundException;
import com.cts.repository.AssignmentRepository;
import com.cts.repository.CourseEnrollmentRepository;
import com.cts.repository.StudentRepository;
import com.cts.repository.SubmissionRepository;
import com.cts.service.FileStorageService;
import com.cts.service.StudentService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final FileStorageService fileStorageService;

    // ── UPDATE STUDENT PROFILE ────────────────────────────────────────

    @Override
    public StudentOutputDTO updateStudentProfile(StudentInputDTO inputDTO) {
        Student student = studentRepository
                .findByUser_UserId(inputDTO.getUserId())
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student profile not found for user id: " + inputDTO.getUserId()
                        + ". Make sure user is registered with role STUDENT."));

        student.setDateOfBirth(inputDTO.getDateOfBirth());
        student.setEducationLevel(inputDTO.getEducationLevel());
        student.setFieldOfInterest(inputDTO.getFieldOfInterest());
        student.setCountry(inputDTO.getCountry());
        student.setBio(inputDTO.getBio());
        student.setEmergencyContact(inputDTO.getEmergencyContact());
        student.setAddressLine(inputDTO.getAddressLine());
        student.setPostalCode(inputDTO.getPostalCode());

        return mapToOutputDTO(studentRepository.save(student));
    }

    // ── GET STUDENT BY ID ─────────────────────────────────────────────

    @Override
    public StudentOutputDTO getStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with id: " + studentId));
        return mapToOutputDTO(student);
    }

    // ── VIEW ASSIGNMENTS FOR A COURSE ─────────────────────────────────
    // Only enrolled students can view assignments

    @Override
    public List<AssignmentOutputDTO> getAssignmentsForCourse(Long studentId,
                                                              Long courseId) {
        verifyEnrollment(studentId, courseId);

        List<Assignment> assignments = assignmentRepository
                .findByCourse_CourseId(courseId);
        if (assignments.isEmpty()) {
            throw new AssignmentNotFoundException(
                    "No assignments found for course id: " + courseId);
        }
        return assignments.stream()
                .map(this::mapAssignmentToOutputDTO)
                .collect(Collectors.toList());
    }

    // ── DOWNLOAD ASSIGNMENT PDF ───────────────────────────────────────
    // Only enrolled students can download

    @Override
    public byte[] downloadAssignmentFile(Long studentId, Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException(
                        "Assignment not found with id: " + assignmentId));

        // Verify student is enrolled in the course this assignment belongs to
        verifyEnrollment(studentId, assignment.getCourse().getCourseId());

        if (assignment.getFilePath() == null || assignment.getFilePath().isBlank()) {
            throw new InvalidFileException(
                    "No PDF file attached to assignment id: " + assignmentId
                    + ". This assignment uses text instructions only.");
        }

        return fileStorageService.loadFile(assignment.getFilePath());
    }

    @Override
    public String getAssignmentFileName(Long studentId, Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException(
                        "Assignment not found with id: " + assignmentId));
        verifyEnrollment(studentId, assignment.getCourse().getCourseId());
        return assignment.getFileName();
    }

    // ── SUBMIT ASSIGNMENT ─────────────────────────────────────────────
    // Only enrolled students can submit

    @Override
    public SubmissionOutputDTO submitAssignment(Long studentId, Long assignmentId,
                                                MultipartFile file) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with id: " + studentId));

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException(
                        "Assignment not found with id: " + assignmentId));

        // Verify enrollment before allowing submission
        verifyEnrollment(studentId, assignment.getCourse().getCourseId());

        LocalDateTime now = LocalDateTime.now();
        String submissionStatus = (assignment.getPublishedAt() != null
                && now.isAfter(assignment.getPublishedAt().plusDays(1)))
                ? "LATE" : "SUBMITTED";

        String savedPath = fileStorageService.storeFile(file, "submissions");

        Submission submission = Submission.builder()
                .student(student)
                .assignment(assignment)
                .course(assignment.getCourse())
                .filePath(savedPath)
                .fileName(file.getOriginalFilename())
                .submittedAt(now)
                .status(submissionStatus)
                .build();

        return mapSubmissionToOutputDTO(submissionRepository.save(submission));
    }

    // ── VIEW MY SUBMISSIONS ───────────────────────────────────────────

    @Override
    public List<SubmissionOutputDTO> getMySubmissions(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with id: " + studentId));

        List<Submission> submissions = submissionRepository
                .findByStudent_StudentId(studentId);
        if (submissions.isEmpty()) {
            throw new SubmissionNotFoundException(
                    "No submissions found for student id: " + studentId);
        }
        return submissions.stream()
                .map(this::mapSubmissionToOutputDTO)
                .collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private void verifyEnrollment(Long studentId, Long courseId) {
        if (!enrollmentRepository
                .existsByStudent_StudentIdAndCourse_CourseId(studentId, courseId)) {
            throw new NotEnrolledException(
                    "Student id " + studentId
                    + " is not enrolled in course id " + courseId
                    + ". Please contact the registrar for enrollment.");
        }
    }

    private StudentOutputDTO mapToOutputDTO(Student student) {
        return StudentOutputDTO.builder()
                .studentId(student.getStudentId())
                .enrollmentNumber(student.getEnrollmentNumber())
                .dateOfBirth(student.getDateOfBirth())
                .educationLevel(student.getEducationLevel())
                .fieldOfInterest(student.getFieldOfInterest())
                .country(student.getCountry())
                .bio(student.getBio())
                .emergencyContact(student.getEmergencyContact())
                .addressLine(student.getAddressLine())
                .postalCode(student.getPostalCode())
                .status(student.getStatus())
                .userId(student.getUser().getUserId())
                .name(student.getUser().getName())
                .email(student.getUser().getEmail())
                .role(student.getUser().getRole().name())
                .build();
    }

    private AssignmentOutputDTO mapAssignmentToOutputDTO(Assignment a) {
        return AssignmentOutputDTO.builder()
                .assignmentId(a.getAssignmentId())
                .title(a.getTitle())
                .instructions(a.getInstructions())
                .fileName(a.getFileName())
                .totalMarks(a.getTotalMarks())
                .publishedAt(a.getPublishedAt())
                .courseId(a.getCourse().getCourseId())
                .courseTitle(a.getCourse().getTitle())
                .build();
    }

    private SubmissionOutputDTO mapSubmissionToOutputDTO(Submission s) {
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