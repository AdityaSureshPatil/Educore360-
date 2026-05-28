package com.cts.serviceimpl;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.AssignmentInputDTO;
import com.cts.dto.AssignmentOutputDTO;
import com.cts.entity.Assignment;
import com.cts.entity.Course;
import com.cts.exception.CourseNotAssignedToInstructorException;
import com.cts.exception.CourseNotFoundException;
import com.cts.exception.InstructorNotFoundException;
import com.cts.repository.AssignmentRepository;
import com.cts.repository.CourseRepository;
import com.cts.repository.InstructorRepository;
import com.cts.service.AssignmentService;
import com.cts.service.FileStorageService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final FileStorageService fileStorageService;

    @Override
    public AssignmentOutputDTO publishAssignment(Long instructorId,
                                                  AssignmentInputDTO inputDTO,
                                                  MultipartFile file) {
        Course course = verifyOwnership(instructorId, inputDTO.getCourseId());

        Assignment assignment = Assignment.builder()
                .title(inputDTO.getTitle())
                .instructions(inputDTO.getInstructions())
                .totalMarks(inputDTO.getTotalMarks())
                .course(course)
                .publishedAt(LocalDateTime.now())
                .build();

        if (file != null && !file.isEmpty()) {
            String filePath = fileStorageService.storeFile(file, "assignments");
            assignment.setFilePath(filePath);
            assignment.setFileName(file.getOriginalFilename());
        }

        return mapToOutputDTO(assignmentRepository.save(assignment));
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private Course verifyOwnership(Long instructorId, Long courseId) {
        if (!instructorRepository.existsById(instructorId)) {
            throw new InstructorNotFoundException(
                    "Instructor not found with id: " + instructorId);
        }
        return courseRepository
                .findByCourseIdAndInstructor_InstructorId(courseId, instructorId)
                .orElseThrow(() -> new CourseNotAssignedToInstructorException(
                        "Course " + courseId + " is not assigned to instructor " + instructorId));
    }

    private AssignmentOutputDTO mapToOutputDTO(Assignment assignment) {
        return AssignmentOutputDTO.builder()
                .assignmentId(assignment.getAssignmentId())
                .title(assignment.getTitle())
                .instructions(assignment.getInstructions())
                .fileName(assignment.getFileName())
                .totalMarks(assignment.getTotalMarks())
                .publishedAt(assignment.getPublishedAt())
                .courseId(assignment.getCourse().getCourseId())
                .courseTitle(assignment.getCourse().getTitle())
                .build();
    }
}