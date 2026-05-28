package com.cts.repository;

import com.cts.entity.ExamCoordinator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamCoordinatorRepository extends JpaRepository<ExamCoordinator, Long> {

    boolean existsByUser_UserId(Long userId);
}