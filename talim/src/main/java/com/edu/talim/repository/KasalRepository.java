package com.edu.talim.repository;

import com.edu.talim.entity.Kasal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KasalRepository extends JpaRepository<Kasal, Long> {

    @Query("""
        SELECT k FROM Kasal k
        LEFT JOIN k.student s
        LEFT JOIN s.course c
        LEFT JOIN s.group g
        WHERE (:kurs IS NULL OR c.kursRaqami = :kurs)
        AND (:guruh IS NULL OR g.guruhNomi = :guruh)
        AND (:fio IS NULL OR s.fio = :fio)
        AND (:jinsi IS NULL OR CAST(s.jinsi AS string) = :jinsi)
        AND (:kasalYuborilganJoy IS NULL OR CAST(k.kasalYuborilganJoy AS string) = :kasalYuborilganJoy)
    """)
    Page<Kasal> findAllWithFilters(
            @Param("kurs") Integer kurs,
            @Param("guruh") String guruh,
            @Param("fio") String fio,
            @Param("jinsi") String jinsi,
            @Param("kasalYuborilganJoy") String kasalYuborilganJoy,
            Pageable pageable
    );
}