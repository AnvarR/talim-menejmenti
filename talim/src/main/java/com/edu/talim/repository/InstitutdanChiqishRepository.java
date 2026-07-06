package com.edu.talim.repository;

import com.edu.talim.entity.InstitutdanChiqish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutdanChiqishRepository extends JpaRepository<InstitutdanChiqish, Long> {

    // chiqganSana bo'yicha filter
    @Query("""
        SELECT c FROM InstitutdanChiqish c
        LEFT JOIN c.student s
        LEFT JOIN s.course k
        LEFT JOIN s.group g
        WHERE c.chiqganSana = :chiqganSana
    """)
    Page<InstitutdanChiqish> findByChiqganSana(
            @Param("chiqganSana") java.time.LocalDate chiqganSana,
            Pageable pageable
    );

    // Barcha ma'lumotlar (filter yo'q)
    @Query("""
        SELECT c FROM InstitutdanChiqish c
    """)
    Page<InstitutdanChiqish> findAllRecords(Pageable pageable);
    boolean existsByStudentId(Long studentId);
}