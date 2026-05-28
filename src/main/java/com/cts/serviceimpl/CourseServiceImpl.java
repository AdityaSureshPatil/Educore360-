package com.cts.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.CourseOutputDTO;
import com.cts.entity.Course;
import com.cts.exception.BusinessException;
import com.cts.exception.CourseNotAssignedToInstructorException;
import com.cts.exception.CourseNotFoundException;
import com.cts.exception.InstructorNotFoundException;
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
    private final FileStorageService fileStorageService;

    @Override
    public List<CourseOutputDTO> getAssignedCourses(Long instructorId) {
        verifyInstructorExists(instructorId);
        List<Course> courses = courseRepository.findByInstructor_InstructorId(instructorId);
        if (courses.isEmpty()) {
            throw new CourseNotFoundException(
                    "No courses assigned to instructor id: " + instructorId);
        }
        return courses.stream()
                .map(this::mapToOutputDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseOutputDTO publishCourseMaterial(Long instructorId, Long courseId,
                                                  MultipartFile file, String textContent) {
        Course course = verifyOwnership(instructorId, courseId);

        if (file != null && !file.isEmpty()) {
            String filePath = fileStorageService.storeFile(file, "materials");
            course.setMaterialFilePath(filePath);
            course.setMaterialFileName(file.getOriginalFilename());
            course.setLearningMaterial(null);
        } else if (textContent != null && !textContent.isBlank()) {
            course.setLearningMaterial(textContent);
            course.setMaterialFilePath(null);
            course.setMaterialFileName(null);
        } else {
            throw new BusinessException("Please provide either a PDF file or text content.");
        }

        course.setPublished(true);
        return mapToOutputDTO(courseRepository.save(course));
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
                        "Course " + courseId + " is not assigned to instructor " + instructorId));
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
                .materialFileName(course.getMaterialFileName())
                .isPublished(course.isPublished())
                .instructorId(course.getInstructor() != null
                        ? course.getInstructor().getInstructorId() : null)
                .instructorName(course.getInstructor() != null
                        ? course.getInstructor().getUser().getName() : null)
                .build();
    }
}