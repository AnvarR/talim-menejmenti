package com.edu.talim.repository;

import com.edu.talim.entity.SutkalikNaryad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SutkalikNaryadRepository extends JpaRepository<SutkalikNaryad, Long> {

    @Query("""
        SELECT n FROM SutkalikNaryad n
        LEFT JOIN n.student s
        LEFT JOIN s.course c
        LEFT JOIN s.group g
        WHERE (:oquvYili IS NULL OR n.oquvYili = :oquvYili)
        AND (:kurs IS NULL OR c.kursRaqami = :kurs)
        AND (:guruh IS NULL OR g.guruhNomi = :guruh)
        AND (:fio IS NULL OR s.fio LIKE %:fio%)
        AND (:xizmatOtashJoyi IS NULL OR CAST(n.xizmatOtashJoyi AS string) = :xizmatOtashJoyi)
        AND (:qabulQilishSanasi IS NULL OR n.qabulQilishSanasi = :qabulQilishSanasi)
        AND (:topshirishSanasi IS NULL OR n.topshirishSanasi = :topshirishSanasi)
    """)
    Page<SutkalikNaryad> findAllWithFilters(
            @Param("oquvYili") String oquvYili,
            @Param("kurs") Integer kurs,
            @Param("guruh") String guruh,
            @Param("fio") String fio,
            @Param("xizmatOtashJoyi") String xizmatOtashJoyi,
            @Param("qabulQilishSanasi") java.time.LocalDate qabulQilishSanasi,
            @Param("topshirishSanasi") java.time.LocalDate topshirishSanasi,
            Pageable pageable
    );
}