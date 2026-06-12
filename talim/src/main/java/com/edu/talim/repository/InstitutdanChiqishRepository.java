package com.edu.talim.repository;

import com.edu.talim.entity.InstitutdanChiqish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InstitutdanChiqishRepository extends JpaRepository<InstitutdanChiqish, Long> {

    @Query("""
        SELECT c FROM InstitutdanChiqish c
        LEFT JOIN c.student s
        LEFT JOIN s.course k
        LEFT JOIN s.group g
        WHERE (:oquvYili IS NULL OR c.oquvYili = :oquvYili)
        AND (:kurs IS NULL OR k.kursRaqami = :kurs)
        AND (:guruh IS NULL OR g.guruhNomi = :guruh)
        AND (:fio IS NULL OR s.fio LIKE %:fio%)
        AND (:chiqishSababi IS NULL OR CAST(c.chiqishSababi AS string) = :chiqishSababi)
        AND (:chiqganSana IS NULL OR c.chiqganSana = :chiqganSana)
    """)
    Page<InstitutdanChiqish> findAllWithFilters(
            @Param("oquvYili") String oquvYili,
            @Param("kurs") Integer kurs,
            @Param("guruh") String guruh,
            @Param("fio") String fio,
            @Param("chiqishSababi") String chiqishSababi,
            @Param("chiqganSana") LocalDate chiqganSana,
            Pageable pageable
    );
}