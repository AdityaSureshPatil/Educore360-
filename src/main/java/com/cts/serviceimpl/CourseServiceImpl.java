package com.cts.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.CourseMaterialFileOutputDTO;
import com.cts.dto.CourseOutputDTO;
import com.cts.entity.Course;
import com.cts.entity.CourseMaterialFile;
import com.cts.exception.BusinessException;
import com.cts.exception.CourseNotAssignedToInstructorException;
import com.cts.exception.CourseNotFoundException;
import com.cts.exception.InstructorNotFoundException;
import com.cts.repository.CourseMaterialFileRepository;
import com.cts.repository.CourseRepository;
import com.cts.repository.InstructorRepository;
import com.cts.service.CourseService;
import com.cts.service.FileStorageService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final CourseMaterialFileRepository materialFileRepository;
    private final FileStorageService fileStorageService;

    // ── GET ASSIGNED COURSES ──────────────────────────────────────────

    @Override
    public List<CourseOutputDTO> getAssignedCourses(Long instructorId) {
        verifyInstructorExists(instructorId);
        List<Course> courses = courseRepository
                .findByInstructor_InstructorId(instructorId);
        if (courses.isEmpty()) {
            throw new CourseNotFoundException(
                    "No courses assigned to instructor id: " + instructorId);
        }
        return courses.stream().map(this::mapToOutputDTO).collect(Collectors.toList());
    }

    // ── PUBLISH COURSE MATERIAL ───────────────────────────────────────
    // PDF  → new row in course_material_file (previous files kept)
    // Text → stored in learning_material field on Course entity

    @Override
    public CourseMaterialFileOutputDTO publishCourseMaterial(Long instructorId,
                                                              Long courseId,
                                                              MultipartFile file,
                                                              String textContent) {
        Course course = verifyOwnership(instructorId, courseId);

        if (file != null && !file.isEmpty()) {

            String savedPath = fileStorageService.storeFile(file, "materials");

            CourseMaterialFile materialFile = CourseMaterialFile.builder()
                    .course(course)
                    .filePath(savedPath)
                    .fileName(file.getOriginalFilename())
                    .uploadedAt(LocalDateTime.now())
                    .build();

            course.setPublished(true);
            courseRepository.save(course);

            CourseMaterialFile saved = materialFileRepository.save(materialFile);
            return mapToMaterialFileOutputDTO(saved);

        } else if (textContent != null && !textContent.isBlank()) {

            course.setLearningMaterial(textContent);
            course.setPublished(true);
            courseRepository.save(course);

            return CourseMaterialFileOutputDTO.builder()
                    .fileId(null)
                    .courseId(course.getCourseId())
                    .courseTitle(course.getTitle())
                    .fileName("text-content")
                    .uploadedAt(LocalDateTime.now())
                    .build();

        } else {
            throw new BusinessException(
                    "Please provide either a PDF file or text content.");
        }
    }

    // ── GET ALL MATERIAL FILES FOR A COURSE ───────────────────────────

    @Override
    public List<CourseMaterialFileOutputDTO> getCourseMaterialFiles(Long courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course not found with id: " + courseId));
        return materialFileRepository.findByCourse_CourseId(courseId)
                .stream()
                .map(this::mapToMaterialFileOutputDTO)
                .collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private void verifyInstructorExists(Long instructorId) {
        if (!instructorRepository.existsById(instructorId)) {
            throw new InstructorNotFoundException(
                    "Instructor not found with id: " + instructorId);
        }
    }

    private Course verifyOwnership(Long instructorId, Long courseId) {
        verifyInstructorExists(instructorId);
        return courseRepository
                .findByCourseIdAndInstructor_InstructorId(courseId, instructorId)
                .orElseThrow(() -> new CourseNotAssignedToInstructorException(
                        "Course " + courseId + " is not assigned to instructor "
                                + instructorId));
    }

    private CourseOutputDTO mapToOutputDTO(Course course) {
        return CourseOutputDTO.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .targetSkill(course.getTargetSkill())
                .credits(course.getCredits())
                .syllabusPath(course.getSyllabusPath())
                .version(course.getVersion())
                .status(course.getStatus())
                .learningMaterial(course.getLearningMaterial())
                .isPublished(course.isPublished())
                .instructorId(course.getInstructor() != null
                        ? course.getInstructor().getInstructorId() : null)
                .instructorName(course.getInstructor() != null
                        ? course.getInstructor().getUser().getName() : null)
                .build();
    }

    private CourseMaterialFileOutputDTO mapToMaterialFileOutputDTO(CourseMaterialFile f) {
        return CourseMaterialFileOutputDTO.builder()
                .fileId(f.getFileId())
                .courseId(f.getCourse().getCourseId())
                .courseTitle(f.getCourse().getTitle())
                .fileName(f.getFileName())
                .uploadedAt(f.getUploadedAt())
                .build();
    }
}