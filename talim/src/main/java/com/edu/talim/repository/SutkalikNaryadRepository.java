package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.SutkalikNaryad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SutkalikNaryadRepository extends JpaRepository<SutkalikNaryad, Long> {

    // Kursantga tegishli naryad yozuvi bormi tekshirish (o'chirishdan oldin)
    boolean existsByStudentId(UUID studentId);

    // Kursant shu kunda naryadda bormi tekshirish (davomat uchun)
    boolean existsByStudentIdAndQabulQilishSanasi(UUID studentId, LocalDate qabulQilishSanasi);

    // Qabul qilish sanasi bo'yicha filter
    @Query("""
        SELECT n FROM SutkalikNaryad n
        LEFT JOIN n.student s
        LEFT JOIN s.course c
        LEFT JOIN s.group g
        WHERE n.qabulQilishSanasi = :qabulQilishSanasi
    """)
    Page<SutkalikNaryad> findByQabulQilishSanasi(
            @Param("qabulQilishSanasi") LocalDate qabulQilishSanasi,
            Pageable pageable
    );

    // Barcha ma'lumotlar (filter yo'q)
    @Query("""
        SELECT n FROM SutkalikNaryad n
    """)
    Page<SutkalikNaryad> findAllRecords(Pageable pageable);
}