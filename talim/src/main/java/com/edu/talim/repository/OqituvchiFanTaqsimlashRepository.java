package com.edu.talim.repository;

import com.edu.talim.entity.OqituvchiFanTaqsimlash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OqituvchiFanTaqsimlashRepository extends JpaRepository<OqituvchiFanTaqsimlash, Long> {

    // Kafedra bo'yicha taqsimlashlar (fan → fanTaqsimlash → fan → kafedra orqali)
    Page<OqituvchiFanTaqsimlash> findByFanTaqsimlashFanKafedraIdOrderByIdDesc(Long kafedraId, Pageable pageable);

    // Dublikat tekshiruvi
    boolean existsByFanTaqsimlashIdAndOqituvchiIdAndDarsTuriAndKursId(
            Long fanTaqsimlashId,
            Long oqituvchiId,
            com.edu.talim.entity.enums.DarsTuri darsTuri,
            Long kursId
    );
}