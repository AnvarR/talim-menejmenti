package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.KursIshiBaho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KursIshiBahoRepository extends JpaRepository<KursIshiBaho, Long> {

    List<KursIshiBaho> findByKursIshiId(Long kursIshiId);

    Optional<KursIshiBaho> findByKursIshiIdAndStudentId(Long kursIshiId, UUID studentId);
}