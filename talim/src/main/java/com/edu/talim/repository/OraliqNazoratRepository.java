package com.edu.talim.repository;

import com.edu.talim.entity.OraliqNazorat;
import com.edu.talim.entity.enums.Semestr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OraliqNazoratRepository extends JpaRepository<OraliqNazorat, Long> {

    // Kursantning shu fan va semestr uchun oraliq nazorati
    Optional<OraliqNazorat> findByOqituvchiFanTaqsimlashIdAndStudentIdAndOquvYiliIdAndSemestr(
            Long oqituvchiFanTaqsimlashId,
            Long studentId,
            Long oquvYiliId,
            Semestr semestr
    );

    // Barcha kursantlarning oraliq nazorat baholari (jadval uchun)
    java.util.List<OraliqNazorat> findByOqituvchiFanTaqsimlashIdAndOquvYiliIdAndSemestr(
            Long oqituvchiFanTaqsimlashId,
            Long oquvYiliId,
            Semestr semestr
    );
}