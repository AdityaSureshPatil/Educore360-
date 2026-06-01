package com.cts.service;

import org.springframework.web.multipart.MultipartFile;
import com.cts.dto.AssignmentFileOutputDTO;
import com.cts.dto.AssignmentInputDTO;
import com.cts.dto.AssignmentOutputDTO;
import java.util.List;

public interface AssignmentService {

    // Each publish creates a new Assignment row
    // Each file upload creates a new AssignmentFile row
    AssignmentOutputDTO publishAssignment(Long instructorId, AssignmentInputDTO inputDTO,
                                          MultipartFile file);

    // Get all uploaded files for a specific assignment
    List<AssignmentFileOutputDTO> getAssignmentFiles(Long assignmentId);
}