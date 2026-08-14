package com.edu.talim.repository;

import com.edu.talim.entity.OqituvchiFanTaqsimlash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OqituvchiFanTaqsimlashRepository extends JpaRepository<OqituvchiFanTaqsimlash, Long> {

    // Kafedra bo'yicha taqsimlashlar (fan → fanTaqsimlash → fan → kafedra orqali)
    Page<OqituvchiFanTaqsimlash> findByFanTaqsimlashFanKafedraIdOrderByIdDesc(Long kafedraId, Pageable pageable);

    // Oraliq/Yakuniyga ruxsat sahifasi uchun - butun fakultet bo'yicha SEMINAR turidagi
    // taqsimlashlar, ixtiyoriy filtrlar bilan (fan/o'qituvchi/kurs/guruh)
    @Query("""
        SELECT DISTINCT t FROM OqituvchiFanTaqsimlash t
        LEFT JOIN t.guruhlar g
        WHERE t.darsTuri = 'SEMINAR'
        AND (:fanId IS NULL OR t.fanTaqsimlash.fan.id = :fanId)
        AND (:oqituvchiId IS NULL OR t.oqituvchi.id = :oqituvchiId)
        AND (:kursId IS NULL OR t.kurs.id = :kursId)
        AND (:guruhId IS NULL OR g.id = :guruhId)
        ORDER BY t.id DESC
    """)
    Page<OqituvchiFanTaqsimlash> findOraliqYakuniyRuxsatRoyxati(
            @Param("fanId") Long fanId,
            @Param("oqituvchiId") Long oqituvchiId,
            @Param("kursId") Long kursId,
            @Param("guruhId") Long guruhId,
            Pageable pageable
    );

    // Bir xil fan+o'qituvchi+dars turi+kurs bo'yicha, GURUHLARI ustma-ust tushadigan
    // mavjud taqsimlashlarni topish (haqiqiy dublikat tekshiruvi).
    // excludeId - tahrirlashda o'zini hisobga olmaslik uchun (yaratishda null yuboriladi)
    @Query("""
        SELECT DISTINCT t FROM OqituvchiFanTaqsimlash t
        JOIN t.guruhlar g
        WHERE t.fanTaqsimlash.id = :fanTaqsimlashId
        AND t.oqituvchi.id = :oqituvchiId
        AND t.darsTuri = :darsTuri
        AND t.kurs.id = :kursId
        AND g.id IN :guruhIds
        AND (:excludeId IS NULL OR t.id <> :excludeId)
    """)
    List<OqituvchiFanTaqsimlash> findGuruhlariUstmaUshtaTushganlar(
            @Param("fanTaqsimlashId") Long fanTaqsimlashId,
            @Param("oqituvchiId") Long oqituvchiId,
            @Param("darsTuri") com.edu.talim.entity.enums.DarsTuri darsTuri,
            @Param("kursId") Long kursId,
            @Param("guruhIds") List<Long> guruhIds,
            @Param("excludeId") Long excludeId
    );

    // Bir xil fan+o'qituvchi+kurs bo'yicha BOSHQA dars turidagi taqsimlashlar
    // (masalan Mustaqil ta'lim uchun Seminar/Amaliyning oraliq kesim sanalarini topish uchun)
    List<OqituvchiFanTaqsimlash> findByFanTaqsimlashIdAndOqituvchiIdAndKursId(
            Long fanTaqsimlashId,
            Long oqituvchiId,
            Long kursId
    );

    // Shu guruhga bog'langan taqsimlashlar bor-yo'qligi (guruhni o'chirishdan oldin tekshirish uchun)
    @Query("SELECT COUNT(t) > 0 FROM OqituvchiFanTaqsimlash t JOIN t.guruhlar g WHERE g.id = :guruhId")
    boolean existsByGuruhId(@Param("guruhId") Long guruhId);

    // Baholash hisobotlari uchun: shu guruh o'qiydigan barcha fanlar (SEMINAR taqsimlashlar)
    List<OqituvchiFanTaqsimlash> findByDarsTuriAndGuruhlarId(
            com.edu.talim.entity.enums.DarsTuri darsTuri, Long guruhlarId);
}