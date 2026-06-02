package com.cts.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.*;
import com.cts.entity.*;
import com.cts.exception.*;
import com.cts.repository.*;
import com.cts.service.FileStorageService;
import com.cts.service.StudentService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentFileRepository assignmentFileRepository;
    private final CourseMaterialFileRepository courseMaterialFileRepository;
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
        if (inputDTO.getDateOfBirth() != null) {
            int age = Period.between(inputDTO.getDateOfBirth(), LocalDate.now()).getYears();
            if (age < 18) {
                throw new BusinessException(
                        "Student must be at least 18 years old. " +
                        "Provided date of birth gives age: " + age + " years.");
            }
        }
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
        return mapToOutputDTO(studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with id: " + studentId)));
    }

    // ── VIEW ALL COURSES ──────────────────────────────────────────────

    @Override
    public List<RegistrarCourseResponseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        if (courses.isEmpty()) {
            throw new NoDetailsAvailableException("No courses available at the moment.");
        }
        return courses.stream().map(this::mapCourseToDTO).collect(Collectors.toList());
    }

    // ── SELF ENROLL ───────────────────────────────────────────────────

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
                .student(student).course(course)
                .enrolledAt(LocalDate.now()).status("ACTIVE").build();
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
        if (enrollments.isEmpty())
            throw new EnrollmentException("You are not enrolled in any courses yet.");
        return enrollments.stream()
                .map(this::mapToEnrollmentOutputDTO).collect(Collectors.toList());
    }

    // ── VIEW ASSIGNMENTS ──────────────────────────────────────────────

    @Override
    public List<AssignmentOutputDTO> getAssignmentsForCourse(Long studentId,
                                                              Long courseId) {
        verifyEnrollment(studentId, courseId);
        List<Assignment> assignments = assignmentRepository
                .findByCourse_CourseId(courseId);
        if (assignments.isEmpty())
            throw new AssignmentNotFoundException(
                    "No assignments found for course id: " + courseId);
        return assignments.stream()
                .map(this::mapAssignmentToOutputDTO).collect(Collectors.toList());
    }

    // ── VIEW ALL COURSE MATERIAL FILES ────────────────────────────────

    @Override
    public List<CourseMaterialFileOutputDTO> getCourseMaterialFiles(Long studentId,
                                                                      Long courseId) {
        verifyEnrollment(studentId, courseId);
        return courseMaterialFileRepository.findByCourse_CourseId(courseId)
                .stream()
                .map(f -> CourseMaterialFileOutputDTO.builder()
                        .fileId(f.getFileId())
                        .courseId(f.getCourse().getCourseId())
                        .courseTitle(f.getCourse().getTitle())
                        .fileName(f.getFileName())
                        .uploadedAt(f.getUploadedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // ── DOWNLOAD COURSE MATERIAL FILE BY fileId ───────────────────────

    @Override
    public byte[] downloadCourseMaterialFile(Long studentId, Long fileId) {
        CourseMaterialFile materialFile = courseMaterialFileRepository
                .findById(fileId)
                .orElseThrow(() -> new InvalidFileException(
                        "Material file not found with id: " + fileId));
        verifyEnrollment(studentId, materialFile.getCourse().getCourseId());
        return fileStorageService.loadFile(materialFile.getFilePath());
    }

    @Override
    public String getCourseMaterialFileName(Long studentId, Long fileId) {
        CourseMaterialFile materialFile = courseMaterialFileRepository
                .findById(fileId)
                .orElseThrow(() -> new InvalidFileException(
                        "Material file not found with id: " + fileId));
        verifyEnrollment(studentId, materialFile.getCourse().getCourseId());
        return materialFile.getFileName();
    }

    // ── VIEW ALL ASSIGNMENT FILES ─────────────────────────────────────

    @Override
    public List<AssignmentFileOutputDTO> getAssignmentFiles(Long studentId,
                                                             Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException(
                        "Assignment not found with id: " + assignmentId));
        verifyEnrollment(studentId, assignment.getCourse().getCourseId());
        return assignmentFileRepository.findByAssignment_AssignmentId(assignmentId)
                .stream()
                .map(f -> AssignmentFileOutputDTO.builder()
                        .fileId(f.getFileId())
                        .assignmentId(f.getAssignment().getAssignmentId())
                        .assignmentTitle(f.getAssignment().getTitle())
                        .fileName(f.getFileName())
                        .uploadedAt(f.getUploadedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // ── DOWNLOAD ASSIGNMENT FILE BY fileId ────────────────────────────

    @Override
    public byte[] downloadAssignmentFile(Long studentId, Long fileId) {
        AssignmentFile assignmentFile = assignmentFileRepository.findById(fileId)
                .orElseThrow(() -> new InvalidFileException(
                        "Assignment file not found with id: " + fileId));
        verifyEnrollment(studentId,
                assignmentFile.getAssignment().getCourse().getCourseId());
        return fileStorageService.loadFile(assignmentFile.getFilePath());
    }

    @Override
    public String getAssignmentFileName(Long studentId, Long fileId) {
        AssignmentFile assignmentFile = assignmentFileRepository.findById(fileId)
                .orElseThrow(() -> new InvalidFileException(
                        "Assignment file not found with id: " + fileId));
        verifyEnrollment(studentId,
                assignmentFile.getAssignment().getCourse().getCourseId());
        return assignmentFile.getFileName();
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
                .student(student).assignment(assignment)
                .course(assignment.getCourse())
                .filePath(savedPath).fileName(file.getOriginalFilename())
                .submittedAt(now).status(submissionStatus).build();
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
        if (submissions.isEmpty())
            throw new SubmissionNotFoundException(
                    "No submissions found for student id: " + studentId);
        return submissions.stream()
                .map(this::mapSubmissionToOutputDTO).collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private void verifyEnrollment(Long studentId, Long courseId) {
        if (!enrollmentRepository
                .existsByStudent_StudentIdAndCourse_CourseId(studentId, courseId)) {
            throw new NotEnrolledException(
                    "You are not enrolled in course id: " + courseId
                    + ". Please enroll first.");
        }
    }

    private RegistrarCourseResponseDTO mapCourseToDTO(Course course) {
        RegistrarCourseResponseDTO.RegistrarCourseResponseDTOBuilder builder =
                RegistrarCourseResponseDTO.builder()
                        .courseId(course.getCourseId())
                        .title(course.getTitle())
                        .credits(course.getCredits())
                        .syllabusPath(course.getSyllabusPath())
                        .version(course.getVersion())
                        .status(course.getStatus());
        if (course.getInstructor() != null) {
            builder.instructorId(course.getInstructor().getInstructorId());
            if (course.getInstructor().getUser() != null)
                builder.instructorName(course.getInstructor().getUser().getName());
        }
        return builder.build();
    }

    private EnrollmentOutputDTO mapToEnrollmentOutputDTO(CourseEnrollment e) {
        return EnrollmentOutputDTO.builder()
                .enrollmentId(e.getEnrollmentId())
                .courseId(e.getCourse().getCourseId())
                .courseTitle(e.getCourse().getTitle())
                .studentId(e.getStudent().getStudentId())
                .studentName(e.getStudent().getUser().getName())
                .enrollmentNumber(e.getStudent().getEnrollmentNumber())
                .enrolledAt(e.getEnrolledAt()).status(e.getStatus()).build();
    }

    private StudentOutputDTO mapToOutputDTO(Student s) {
        return StudentOutputDTO.builder()
                .studentId(s.getStudentId())
                .enrollmentNumber(s.getEnrollmentNumber())
                .dateOfBirth(s.getDateOfBirth())
                .educationLevel(s.getEducationLevel())
                .fieldOfInterest(s.getFieldOfInterest())
                .country(s.getCountry()).bio(s.getBio())
                .emergencyContact(s.getEmergencyContact())
                .addressLine(s.getAddressLine()).postalCode(s.getPostalCode())
                .status(s.getStatus())
                .userId(s.getUser().getUserId()).name(s.getUser().getName())
                .email(s.getUser().getEmail()).role(s.getUser().getRole().name())
                .build();
    }

    // fileName removed — Assignment no longer has this field
    // Use getAssignmentFiles() to list files per assignment
    private AssignmentOutputDTO mapAssignmentToOutputDTO(Assignment a) {
        return AssignmentOutputDTO.builder()
                .assignmentId(a.getAssignmentId())
                .title(a.getTitle())
                .instructions(a.getInstructions())
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
                .fileName(s.getFileName()).textContent(s.getTextContent())
                .submittedAt(s.getSubmittedAt()).grade(s.getGrade())
                .feedback(s.getFeedback()).gradedAt(s.getGradedAt())
                .status(s.getStatus())
                .assignmentId(s.getAssignment().getAssignmentId())
                .assignmentTitle(s.getAssignment().getTitle())
                .courseId(s.getCourse().getCourseId())
                .courseTitle(s.getCourse().getTitle()).build();
    }
}