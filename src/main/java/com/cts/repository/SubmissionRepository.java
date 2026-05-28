package com.cts.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cts.entity.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // Used by instructor: all submissions for a course
    List<Submission> findByCourse_CourseId(Long courseId);

    // Used by instructor: all submissions for a specific assignment
    List<Submission> findByAssignment_AssignmentId(Long assignmentId);

    // Used by student: their own submissions
    List<Submission> findByStudent_StudentId(Long studentId);
}