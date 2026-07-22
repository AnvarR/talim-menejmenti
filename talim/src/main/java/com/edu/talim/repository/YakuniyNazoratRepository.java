package com.edu.talim.repository;

import com.edu.talim.entity.YakuniyNazorat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YakuniyNazoratRepository extends JpaRepository<YakuniyNazorat, Long> {

    // Kursantning shu fan uchun yakuniy nazorati
    Optional<YakuniyNazorat> findByOqituvchiFanTaqsimlashIdAndStudentIdAndOquvYiliId(
            Long oqituvchiFanTaqsimlashId,
            Long studentId,
            Long oquvYiliId
    );

    // Barcha kursantlarning yakuniy nazorat baholari (jadval uchun)
    List<YakuniyNazorat> findByOqituvchiFanTaqsimlashIdAndOquvYiliId(
            Long oqituvchiFanTaqsimlashId,
            Long oquvYiliId
    );
}