package com.cts.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cts.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUser_UserId(Long userId);

    boolean existsByUser_UserId(Long userId);

    boolean existsByEnrollmentNumber(String enrollmentNumber);
}