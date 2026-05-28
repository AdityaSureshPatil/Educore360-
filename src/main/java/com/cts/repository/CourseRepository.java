package com.cts.repository;

import com.cts.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByStatus(String status);

    boolean existsByTitleIgnoreCase(String title);

    List<Course> findByInstructor_InstructorId(Long instructorId);

    Optional<Course> findByCourseIdAndInstructor_InstructorId(Long courseId,
                                                               Long instructorId);

    // Live count of courses that have an instructor assigned
    int countByInstructor_InstructorIdIsNotNull();
}