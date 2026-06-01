package com.cts.service;

import com.cts.dto.AssignmentFileOutputDTO;
import com.cts.dto.AssignmentOutputDTO;
import com.cts.dto.CourseMaterialFileOutputDTO;
import com.cts.dto.EnrollmentOutputDTO;
import com.cts.dto.RegistrarCourseResponseDTO;
import com.cts.dto.StudentInputDTO;
import com.cts.dto.StudentOutputDTO;
import com.cts.dto.SubmissionOutputDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface StudentService {

    StudentOutputDTO updateStudentProfile(StudentInputDTO inputDTO);

    StudentOutputDTO getStudentById(Long studentId);

    List<RegistrarCourseResponseDTO> getAllCourses();

    EnrollmentOutputDTO enrollInCourse(Long studentId, Long courseId);

    List<EnrollmentOutputDTO> getMyEnrolledCourses(Long studentId);

    List<AssignmentOutputDTO> getAssignmentsForCourse(Long studentId, Long courseId);

    // View all material files for an enrolled course
    List<CourseMaterialFileOutputDTO> getCourseMaterialFiles(Long studentId,
                                                              Long courseId);

    // Download specific material file by fileId
    byte[] downloadCourseMaterialFile(Long studentId, Long fileId);
    String getCourseMaterialFileName(Long studentId, Long fileId);

    // View all files for an assignment
    List<AssignmentFileOutputDTO> getAssignmentFiles(Long studentId, Long assignmentId);

    // Download specific assignment file by fileId
    byte[] downloadAssignmentFile(Long studentId, Long fileId);
    String getAssignmentFileName(Long studentId, Long fileId);

    SubmissionOutputDTO submitAssignment(Long studentId, Long assignmentId,
                                         MultipartFile file);

    List<SubmissionOutputDTO> getMySubmissions(Long studentId);
}