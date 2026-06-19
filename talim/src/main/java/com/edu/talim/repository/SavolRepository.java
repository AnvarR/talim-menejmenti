package com.edu.talim.repository;

import com.edu.talim.entity.Savol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SavolRepository extends JpaRepository<Savol, Long> {

    // Barcha savollar — eng yangi avval (sahifalash bilan)
    Page<Savol> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Muallif bo'yicha savollar
    Page<Savol> findByAuthorIdAndAuthorTypeOrderByCreatedAtDesc(
            Long authorId, String authorType, Pageable pageable
    );

    // Fayl yuklangan savollar sonini hisoblash (faylUrl bo'sh bo'lmaganlar)
    long countByFaylUrlIsNotNull();

    // Barcha savollarning ko'rishlar sonini yig'ish
    @Query("SELECT COALESCE(SUM(s.korishlarSoni), 0) FROM Savol s")
    long sumKorishlarSoni();
}