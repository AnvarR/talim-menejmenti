package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.YakuniyNazorat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YakuniyNazoratRepository extends JpaRepository<YakuniyNazorat, Long> {

    Optional<YakuniyNazorat> findByOqituvchiFanTaqsimlashIdAndStudentIdAndOquvYiliId(
            UUID oqituvchiFanTaqsimlashId, UUID studentId, Long oquvYiliId);

    List<YakuniyNazorat> findByOqituvchiFanTaqsimlashIdAndOquvYiliId(
            UUID oqituvchiFanTaqsimlashId, Long oquvYiliId);

    // Reyting daftarchasi uchun: shu kursantning barcha yakuniy nazorat yozuvlari
    List<YakuniyNazorat> findByStudentId(UUID studentId);
}