package com.cts.service;

import com.cts.dto.AssignmentOutputDTO;
import com.cts.dto.StudentInputDTO;
import com.cts.dto.StudentOutputDTO;
import com.cts.dto.SubmissionOutputDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface StudentService {

    StudentOutputDTO updateStudentProfile(StudentInputDTO inputDTO);

    StudentOutputDTO getStudentById(Long studentId);

    // Only enrolled students can view assignments
    List<AssignmentOutputDTO> getAssignmentsForCourse(Long studentId, Long courseId);

    // Only enrolled students can download
    byte[] downloadAssignmentFile(Long studentId, Long assignmentId);

    String getAssignmentFileName(Long studentId, Long assignmentId);

    // Only enrolled students can submit
    SubmissionOutputDTO submitAssignment(Long studentId, Long assignmentId,
                                         MultipartFile file);

    List<SubmissionOutputDTO> getMySubmissions(Long studentId);
}