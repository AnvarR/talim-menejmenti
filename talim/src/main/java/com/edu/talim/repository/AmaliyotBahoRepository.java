package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.AmaliyotBaho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmaliyotBahoRepository extends JpaRepository<AmaliyotBaho, UUID> {

    List<AmaliyotBaho> findByAmaliyotId(UUID amaliyotId);

    Optional<AmaliyotBaho> findByAmaliyotIdAndStudentId(UUID amaliyotId, UUID studentId);
}