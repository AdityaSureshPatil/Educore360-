package com.cts.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.AssignmentOutputDTO;
import com.cts.dto.EnrollmentOutputDTO;
import com.cts.dto.StudentInputDTO;
import com.cts.dto.StudentOutputDTO;
import com.cts.dto.SubmissionOutputDTO;
import com.cts.entity.Assignment;
import com.cts.entity.Course;
import com.cts.entity.CourseEnrollment;
import com.cts.entity.Student;
import com.cts.entity.Submission;
import com.cts.exception.AssignmentNotFoundException;
import com.cts.exception.CourseNotFoundException;
import com.cts.exception.EnrollmentException;
import com.cts.exception.InvalidFileException;
import com.cts.exception.NotEnrolledException;
import com.cts.exception.StudentNotFoundException;
import com.cts.exception.SubmissionNotFoundException;
import com.cts.repository.AssignmentRepository;
import com.cts.repository.CourseEnrollmentRepository;
import com.cts.repository.CourseRepository;
import com.cts.repository.StudentRepository;
import com.cts.repository.SubmissionRepository;
import com.cts.service.FileStorageService;
import com.cts.service.StudentService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
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

    // ── SELF ENROLL IN COURSE ─────────────────────────────────────────

    @Override
    public EnrollmentOutputDTO enrollInCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with id: " + studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course not found with id: " + courseId));
        if (enrollmentRepository
                .existsByStudent_StudentIdAndCourse_CourseId(studentId, courseId)) {
            throw new EnrollmentException(
                    "You are already enrolled in course: " + course.getTitle());
        }
        CourseEnrollment enrollment = CourseEnrollment.builder()
                .student(student)
                .course(course)
                .enrolledAt(LocalDate.now())
                .status("ACTIVE")
                .build();
        return mapToEnrollmentOutputDTO(enrollmentRepository.save(enrollment));
    }

    // ── VIEW MY ENROLLED COURSES ──────────────────────────────────────

    @Override
    public List<EnrollmentOutputDTO> getMyEnrolledCourses(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with id: " + studentId));
        List<CourseEnrollment> enrollments = enrollmentRepository
                .findByStudent_StudentId(studentId);
        if (enrollments.isEmpty()) {
            throw new EnrollmentException("You are not enrolled in any courses yet.");
        }
        return enrollments.stream()
                .map(this::mapToEnrollmentOutputDTO)
                .collect(Collectors.toList());
    }

    // ── VIEW ASSIGNMENTS FOR A COURSE ─────────────────────────────────

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

    // ── DOWNLOAD COURSE MATERIAL PDF ──────────────────────────────────

    @Override
    public byte[] downloadCourseMaterial(Long studentId, Long courseId) {
        // Verify enrollment first
        verifyEnrollment(studentId, courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course not found with id: " + courseId));

        // Check if material is a PDF file
        if (course.getMaterialFilePath() == null
                || course.getMaterialFilePath().isBlank()) {
            throw new InvalidFileException(
                    "No PDF material available for course: " + course.getTitle()
                    + ". This course uses text-based material only.");
        }

        return fileStorageService.loadFile(course.getMaterialFilePath());
    }

    @Override
    public String getCourseMaterialFileName(Long studentId, Long courseId) {
        verifyEnrollment(studentId, courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course not found with id: " + courseId));
        return course.getMaterialFileName();
    }

    // ── DOWNLOAD ASSIGNMENT PDF ───────────────────────────────────────

    @Override
    public byte[] downloadAssignmentFile(Long studentId, Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException(
                        "Assignment not found with id: " + assignmentId));
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

    @Override
    public SubmissionOutputDTO submitAssignment(Long studentId, Long assignmentId,
                                                MultipartFile file) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with id: " + studentId));
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException(
                        "Assignment not found with id: " + assignmentId));
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
                    "You are not enrolled in course id: " + courseId
                    + ". Please enroll first via POST /student/{studentId}/course/{courseId}/enroll");
        }
    }

    private EnrollmentOutputDTO mapToEnrollmentOutputDTO(CourseEnrollment e) {
        return EnrollmentOutputDTO.builder()
                .enrollmentId(e.getEnrollmentId())
                .courseId(e.getCourse().getCourseId())
                .courseTitle(e.getCourse().getTitle())
                .studentId(e.getStudent().getStudentId())
                .studentName(e.getStudent().getUser().getName())
                .enrollmentNumber(e.getStudent().getEnrollmentNumber())
                .enrolledAt(e.getEnrolledAt())
                .status(e.getStatus())
                .build();
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