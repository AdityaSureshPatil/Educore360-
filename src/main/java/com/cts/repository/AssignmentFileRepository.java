package com.cts.repository;

import com.cts.entity.AssignmentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssignmentFileRepository extends JpaRepository<AssignmentFile, Long> {

    List<AssignmentFile> findByAssignment_AssignmentId(Long assignmentId);
}