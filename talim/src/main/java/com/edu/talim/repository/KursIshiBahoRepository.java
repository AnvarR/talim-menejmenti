package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.KursIshiBaho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KursIshiBahoRepository extends JpaRepository<KursIshiBaho, UUID> {

    List<KursIshiBaho> findByKursIshiId(UUID kursIshiId);

    Optional<KursIshiBaho> findByKursIshiIdAndStudentId(UUID kursIshiId, UUID studentId);
}