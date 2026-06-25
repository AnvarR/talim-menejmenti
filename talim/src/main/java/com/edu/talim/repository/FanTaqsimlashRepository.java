package com.edu.talim.repository;

import com.edu.talim.entity.FanTaqsimlash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FanTaqsimlashRepository extends JpaRepository<FanTaqsimlash, Long> {

    // Sahifalash bilan barcha taqsimlashlar
    Page<FanTaqsimlash> findAllByOrderByIdDesc(Pageable pageable);

    // Dublikat tekshiruvi — oqituvchisiz
    boolean existsByFanIdAndKursIdAndGuruhIdAndSoatHajmiAndMarruzaSoatiAndSeminarSoatiAndMustaqilTalimSoatiAndAmaliyotMavjudAndKursIshiMavjud(
            Long fanId,
            Long kursId,
            Long guruhId,
            Integer soatHajmi,
            Integer marruzaSoati,
            Integer seminarSoati,
            Integer mustaqilTalimSoati,
            Boolean amaliyotMavjud,
            Boolean kursIshiMavjud
    );
}