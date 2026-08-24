package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.DarsJurnali;
import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.entity.enums.Semestr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DarsJurnaliRepository extends JpaRepository<DarsJurnali, UUID> {

    // O'qituvchi fan taqsimlash, dars turi va o'quv yili bo'yicha darslar
    List<DarsJurnali> findByOqituvchiFanTaqsimlashIdAndDarsTuriAndOquvYiliId(
            UUID oqituvchiFanTaqsimlashId, DarsTuri darsTuri, Long oquvYiliId);

    // Semestr bo'yicha ham filtrlangan darslar
    List<DarsJurnali> findByOqituvchiFanTaqsimlashIdAndDarsTuriAndOquvYiliIdAndSemestr(
            UUID oqituvchiFanTaqsimlashId, DarsTuri darsTuri, Long oquvYiliId, Semestr semestr);

    // Bir xil sana va taqsimlashda dars bormi tekshirish
    Optional<DarsJurnali> findByOqituvchiFanTaqsimlashIdAndDarsTuriAndSana(
            UUID oqituvchiFanTaqsimlashId, DarsTuri darsTuri, LocalDate sana);

    // Blok tekshiruvi uchun: kursantning so'nggi N/K/S/Y davomati
    @Query("""
        SELECT d FROM Davomat d
        WHERE d.student.id = :studentId
        AND d.holat IN ('N', 'K', 'S', 'Y')
        AND d.bloklanganMi = false
        AND d.darsJurnali.sana <= :bugun
        AND d.darsJurnali.sana >= :yettaKunOldin
    """)
    List<com.edu.talim.entity.Davomat> findQaytaTopshirishKeraklar(
            @Param("studentId") UUID studentId,
            @Param("bugun") LocalDate bugun,
            @Param("yettaKunOldin") LocalDate yettaKunOldin
    );
}