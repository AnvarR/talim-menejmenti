package com.edu.talim.repository;

import com.edu.talim.entity.AmaliyDavomat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AmaliyDavomatRepository extends JpaRepository<AmaliyDavomat, Long> {

    // Bitta dars uchun barcha davomatlar
    List<AmaliyDavomat> findByDarsJurnaliIdOrderByStudentFioAsc(Long darsJurnaliId);

    // Bitta kursantning bitta darsdagi davomati
    Optional<AmaliyDavomat> findByDarsJurnaliIdAndStudentId(Long darsJurnaliId, Long studentId);

    // Kursantning bitta fan taqsimlashdagi barcha davomatlari (R(KB) hisoblash uchun)
    // Diqqat: baho YOKI qaytaTopshirishBaho bo'lgan yozuvlar ham hisobga olinadi,
    // chunki kursant darsda qatnashmasdan (N/K/S/Y) keyin faqat qayta topshirish
    // bahosiga ega bo'lishi mumkin (baho maydoni bo'sh qoladi).
    @Query("""
        SELECT d FROM AmaliyDavomat d
        WHERE d.student.id = :studentId
        AND d.darsJurnali.oqituvchiFanTaqsimlash.id = :taqsimlashId
        AND d.darsJurnali.sana BETWEEN :boshlanish AND :tugash
        AND (d.baho IS NOT NULL OR d.qaytaTopshirishBaho IS NOT NULL)
    """)
    List<AmaliyDavomat> findBaholangan(
            @Param("studentId") Long studentId,
            @Param("taqsimlashId") Long taqsimlashId,
            @Param("boshlanish") LocalDate boshlanish,
            @Param("tugash") LocalDate tugash
    );

    // 7 kundan oshgan va qayta topshirilmagan darslar (blok uchun)
    @Query("""
        SELECT d FROM AmaliyDavomat d
        WHERE d.student.id = :studentId
        AND d.bloklanganMi = false
        AND d.qaytaTopshirishBaho IS NULL
        AND (d.holat IN ('N', 'K', 'S', 'Y') OR d.baho = 2)
        AND d.darsJurnali.sana <= :yettaKunOldin
    """)
    List<AmaliyDavomat> findBloklashKeraklar(
            @Param("studentId") Long studentId,
            @Param("yettaKunOldin") LocalDate yettaKunOldin
    );

    // Kursantning bloklanganmi tekshirish
    @Query("""
        SELECT COUNT(d) > 0 FROM AmaliyDavomat d
        WHERE d.student.id = :studentId
        AND d.bloklanganMi = true
    """)
    boolean isStudentBloklangan(@Param("studentId") Long studentId);
}