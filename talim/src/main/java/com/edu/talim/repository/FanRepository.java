package com.edu.talim.repository;

import com.edu.talim.entity.Fan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FanRepository extends JpaRepository<Fan, Long> {

    /**
     * Kafedra, fan nomi, kurs va guruh bo'yicha filter qilib fanlar ro'yxatini qaytaradi.
     */
    @Query("""
        SELECT f FROM Fan f
        LEFT JOIN f.kafedra k
        LEFT JOIN f.kurs kr
        LEFT JOIN f.guruh g
        WHERE (:kafedraId IS NULL OR k.id = :kafedraId)
        AND (:fanNomi IS NULL OR f.fanNomi LIKE %:fanNomi%)
        AND (:kursId IS NULL OR kr.id = :kursId)
        AND (:guruhId IS NULL OR g.id = :guruhId)
    """)
    Page<Fan> findAllWithFilters(
            @Param("kafedraId") Long kafedraId,
            @Param("fanNomi") String fanNomi,
            @Param("kursId") Long kursId,
            @Param("guruhId") Long guruhId,
            Pageable pageable
    );
}