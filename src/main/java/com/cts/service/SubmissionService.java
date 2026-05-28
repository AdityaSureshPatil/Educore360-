package com.cts.service;

import java.util.List;
import com.cts.dto.GradeInputDTO;
import com.cts.dto.SubmissionOutputDTO;

public interface SubmissionService {

    List<SubmissionOutputDTO> getSubmissionsForCourse(Long instructorId, Long courseId);

    SubmissionOutputDTO gradeSubmission(Long instructorId, Long submissionId,
                                        GradeInputDTO gradeInputDTO);

    // Download student submitted PDF
    byte[] downloadSubmissionFile(Long instructorId, Long submissionId);

    String getSubmissionFileName(Long instructorId, Long submissionId);
}