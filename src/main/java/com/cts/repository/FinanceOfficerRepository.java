package com.cts.repository;

import com.cts.entity.FinanceOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceOfficerRepository extends JpaRepository<FinanceOfficer, Long> {

    boolean existsByUser_UserId(Long userId);
}