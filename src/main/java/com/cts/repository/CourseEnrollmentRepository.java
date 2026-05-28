package com.cts.repository;

import com.cts.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    // Check if a student is enrolled in a specific course
    boolean existsByStudent_StudentIdAndCourse_CourseId(Long studentId, Long courseId);

    // All enrollments for a specific course
    List<CourseEnrollment> findByCourse_CourseId(Long courseId);

    // All courses a specific student is enrolled in
    List<CourseEnrollment> findByStudent_StudentId(Long studentId);
}