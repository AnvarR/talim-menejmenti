package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.Davomat;
import com.edu.talim.entity.enums.DavomatHolati;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DavomatRepository extends JpaRepository<Davomat, UUID> {

    // Bitta dars uchun barcha davomatlar
    List<Davomat> findByDarsJurnaliId(UUID darsJurnaliId);

    // Bitta kursantning bitta darsdagi davomati
    Optional<Davomat> findByDarsJurnaliIdAndStudentId(UUID darsJurnaliId, UUID studentId);

    // 7 kundan oshgan va qayta topshirilmagan darslar
    // Ya'ni: dars sanasi bugundan 7 kun oldin bo'lgan va holat N/K/S/Y bo'lgan
    @Query("""
        SELECT d FROM Davomat d
        WHERE d.student.id = :studentId
        AND d.holat IN ('N', 'K', 'S', 'Y')
        AND d.bloklanganMi = false
        AND d.darsJurnali.sana <= :yettaKunOldin
    """)
    List<Davomat> findBloklashKeraklar(
            @Param("studentId") UUID studentId,
            @Param("yettaKunOldin") LocalDate yettaKunOldin
    );

    // Kursantning barcha qoldirgan darslari (7 kun ichida — hali bloklanmagan)
    @Query("""
        SELECT d FROM Davomat d
        WHERE d.student.id = :studentId
        AND d.holat IN ('N', 'K', 'S', 'Y')
        AND d.bloklanganMi = false
        AND d.darsJurnali.sana BETWEEN :yettaKunOldin AND :bugun
    """)
    List<Davomat> findQoldirganDarslar(
            @Param("studentId") UUID studentId,
            @Param("bugun") LocalDate bugun,
            @Param("yettaKunOldin") LocalDate yettaKunOldin
    );

    // Kursantning bloklanganmi tekshirish
    @Query("""
        SELECT COUNT(d) > 0 FROM Davomat d
        WHERE d.student.id = :studentId
        AND d.bloklanganMi = true
    """)
    boolean isStudentBloklangan(@Param("studentId") UUID studentId);

    // Kursantning bitta dars turida davomatlari (hisobot uchun)
    @Query("""
        SELECT d FROM Davomat d
        WHERE d.student.id = :studentId
        AND d.darsJurnali.oqituvchiFanTaqsimlash.id = :taqsimlashId
        AND d.holat IS NOT NULL
    """)
    List<Davomat> findByStudentAndTaqsimlash(
            @Param("studentId") UUID studentId,
            @Param("taqsimlashId") UUID taqsimlashId
    );

    boolean existsByStudentIdAndDarsJurnaliSanaAndHolatIn(
            UUID studentId, LocalDate sana, List<DavomatHolati> holatlar
    );
}