package com.cts.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.cts.dto.EnrollmentOutputDTO;
import com.cts.dto.RegistrarCourseCreateDTO;
import com.cts.dto.RegistrarCourseResponseDTO;
import com.cts.entity.Course;
import com.cts.entity.Instructor;
import com.cts.exception.AcademicException;
import com.cts.exception.CourseAlreadyExistsException;
import com.cts.exception.CourseNotFoundException;
import com.cts.exception.InstructorNotFoundException;
import com.cts.repository.CourseEnrollmentRepository;
import com.cts.repository.CourseRepository;
import com.cts.repository.InstructorRepository;
import com.cts.service.RegistrarAcademicService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrarAcademicServiceImpl implements RegistrarAcademicService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final CourseEnrollmentRepository enrollmentRepository;

    // ── CREATE COURSE ─────────────────────────────────────────────────

    @Override
    public RegistrarCourseResponseDTO provisionNewCourse(
            RegistrarCourseCreateDTO createDTO) {
        if (courseRepository.existsByTitleIgnoreCase(createDTO.getTitle())) {
            throw new CourseAlreadyExistsException(
                    "A course titled '" + createDTO.getTitle() + "' already exists.");
        }
        Course course = Course.builder()
                .title(createDTO.getTitle())
                .credits(createDTO.getCredits())
                .syllabusPath(createDTO.getSyllabusPath())
                .version(createDTO.getVersion())
                .status("DRAFT")
                .isPublished(false)
                .instructor(null)
                .build();
        return mapToResponseDTO(courseRepository.save(course));
    }

    // ── ASSIGN INSTRUCTOR ─────────────────────────────────────────────

    @Override
    public RegistrarCourseResponseDTO assignInstructorToCourse(Long courseId,
                                                                Long instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AcademicException(
                        "Course not found with id: " + courseId));
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new InstructorNotFoundException(
                        "Instructor not found with id: " + instructorId));
        if (instructor.getUser() != null
                && !"ACTIVE".equalsIgnoreCase(instructor.getUser().getStatus())) {
            throw new AcademicException(
                    "Cannot assign an INACTIVE instructor to a course.");
        }
        course.setInstructor(instructor);
        return mapToResponseDTO(courseRepository.save(course));
    }

    // ── GET ALL COURSES ───────────────────────────────────────────────

    @Override
    public List<RegistrarCourseResponseDTO> getAllConfiguredCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ── VIEW ENROLLED STUDENTS (read-only for registrar) ─────────────

    @Override
    public List<EnrollmentOutputDTO> getEnrolledStudents(Long courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course not found with id: " + courseId));
        return enrollmentRepository.findByCourse_CourseId(courseId)
                .stream()
                .map(e -> EnrollmentOutputDTO.builder()
                        .enrollmentId(e.getEnrollmentId())
                        .courseId(e.getCourse().getCourseId())
                        .courseTitle(e.getCourse().getTitle())
                        .studentId(e.getStudent().getStudentId())
                        .studentName(e.getStudent().getUser().getName())
                        .enrollmentNumber(e.getStudent().getEnrollmentNumber())
                        .enrolledAt(e.getEnrolledAt())
                        .status(e.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    // ── Helper ────────────────────────────────────────────────────────

    private RegistrarCourseResponseDTO mapToResponseDTO(Course course) {
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
            if (course.getInstructor().getUser() != null) {
                builder.instructorName(course.getInstructor().getUser().getName());
            }
        }
        return builder.build();
    }
}