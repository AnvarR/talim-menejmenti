package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.MustaqilTalimTopshiriq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MustaqilTalimTopshiriqRepository extends JpaRepository<MustaqilTalimTopshiriq, UUID> {

    // Bitta mavzuga tegishli barcha topshiriqlar (Topshiriqlar ro'yxati sahifasi uchun)
    List<MustaqilTalimTopshiriq> findByDarsJurnaliIdOrderByYaratilganVaqtAsc(UUID darsJurnaliId);

    // Bitta fan taqsimlashga tegishli barcha topshiriqlar (fanlar ro'yxatida "Topshiriqlar"/"Topshiriq tili" ustunlari uchun)
    List<MustaqilTalimTopshiriq> findByOqituvchiFanTaqsimlashId(UUID oqituvchiFanTaqsimlashId);

    // Bir nechta "birodar" taqsimlashlar (bir xil fanTaqsimlash+guruh, lekin turli darsTuri) bo'yicha topshiriqlar -
    // frontend qaysi darsTuriga topshiriq yozishidan qat'i nazar barchasini topish uchun ishlatiladi
    List<MustaqilTalimTopshiriq> findByOqituvchiFanTaqsimlashIdIn(List<UUID> oqituvchiFanTaqsimlashIds);
}