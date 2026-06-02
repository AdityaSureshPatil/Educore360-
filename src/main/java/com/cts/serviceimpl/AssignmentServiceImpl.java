package com.cts.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.AssignmentFileOutputDTO;
import com.cts.dto.AssignmentInputDTO;
import com.cts.dto.AssignmentOutputDTO;
import com.cts.entity.Assignment;
import com.cts.entity.AssignmentFile;
import com.cts.entity.Course;
import com.cts.exception.AssignmentNotFoundException;
import com.cts.exception.CourseNotAssignedToInstructorException;
import com.cts.exception.CourseNotFoundException;
import com.cts.exception.InstructorNotFoundException;
import com.cts.repository.AssignmentFileRepository;
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
    private final AssignmentFileRepository assignmentFileRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final FileStorageService fileStorageService;

    // ── PUBLISH ASSIGNMENT ────────────────────────────────────────────
    // Each call creates a NEW Assignment row
    // Each file creates a NEW row in assignment_file table
    // Previous assignments and files are NEVER overwritten

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

        Assignment saved = assignmentRepository.save(assignment);

        // If file provided — save new row in assignment_file table only
        if (file != null && !file.isEmpty()) {
            String savedPath = fileStorageService.storeFile(file, "assignments");

            AssignmentFile assignmentFile = AssignmentFile.builder()
                    .assignment(saved)
                    .filePath(savedPath)
                    .fileName(file.getOriginalFilename())
                    .uploadedAt(LocalDateTime.now())
                    .build();

            assignmentFileRepository.save(assignmentFile);
        }

        return mapToOutputDTO(saved);
    }

    // ── GET ALL FILES FOR AN ASSIGNMENT ───────────────────────────────

    @Override
    public List<AssignmentFileOutputDTO> getAssignmentFiles(Long assignmentId) {
        assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new AssignmentNotFoundException(
                        "Assignment not found with id: " + assignmentId));
        return assignmentFileRepository
                .findByAssignment_AssignmentId(assignmentId)
                .stream()
                .map(this::mapToFileOutputDTO)
                .collect(Collectors.toList());
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
                        "Course " + courseId + " is not assigned to instructor "
                                + instructorId));
    }

    private AssignmentOutputDTO mapToOutputDTO(Assignment a) {
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

    private AssignmentFileOutputDTO mapToFileOutputDTO(AssignmentFile f) {
        return AssignmentFileOutputDTO.builder()
                .fileId(f.getFileId())
                .assignmentId(f.getAssignment().getAssignmentId())
                .assignmentTitle(f.getAssignment().getTitle())
                .fileName(f.getFileName())
                .uploadedAt(f.getUploadedAt())
                .build();
    }
}