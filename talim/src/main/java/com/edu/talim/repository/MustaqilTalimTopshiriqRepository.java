package com.edu.talim.repository;

import com.edu.talim.entity.MustaqilTalimTopshiriq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MustaqilTalimTopshiriqRepository extends JpaRepository<MustaqilTalimTopshiriq, Long> {

    // Bitta mavzuga tegishli barcha topshiriqlar (Topshiriqlar ro'yxati sahifasi uchun)
    List<MustaqilTalimTopshiriq> findByDarsJurnaliIdOrderByYaratilganVaqtAsc(Long darsJurnaliId);

    // Bitta fan taqsimlashga tegishli barcha topshiriqlar (fanlar ro'yxatida "Topshiriqlar"/"Topshiriq tili" ustunlari uchun)
    List<MustaqilTalimTopshiriq> findByOqituvchiFanTaqsimlashId(Long oqituvchiFanTaqsimlashId);
}