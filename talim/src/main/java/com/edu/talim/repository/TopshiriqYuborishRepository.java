package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.TopshiriqYuborish;
import com.edu.talim.entity.enums.TopshiriqHolati;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopshiriqYuborishRepository extends JpaRepository<TopshiriqYuborish, Long> {

    // Bitta topshiriqqa tegishli barcha yuborishlar (kursantlar ro'yxati, muddat belgilash uchun)
    List<TopshiriqYuborish> findByTopshiriqId(Long topshiriqId);

    // Bitta kursantga shu topshiriq bo'yicha yuborilgan nusxa
    List<TopshiriqYuborish> findByTopshiriqIdAndStudentId(Long topshiriqId, UUID studentId);

    // Bitta mavzuga (darsJurnali) tegishli BARCHA topshiriqlar bo'yicha yuborishlar
    // ("Topshiriq holati" umumiy ro'yxati uchun - turli topshiriq turlari aralash chiqadi)
    List<TopshiriqYuborish> findByTopshiriq_DarsJurnaliId(UUID darsJurnaliId);

    // Bitta kursantga yuborilgan BARCHA topshiriqlar (kursant tarafidagi ro'yxat uchun)
    List<TopshiriqYuborish> findByStudentId(UUID studentId);

    // Bitta kursantga, aniq bir mavzu (darsJurnali) bo'yicha yuborilgan topshiriqlar
    List<TopshiriqYuborish> findByStudentIdAndTopshiriq_DarsJurnaliId(UUID studentId, UUID darsJurnaliId);

    // Status bo'yicha hisoblash: jami yuborilganlar soni
    long countByTopshiriqId(Long topshiriqId);

    // Hali javob bermaganlar (holati=BERILDI) dan farqli, ya'ni javob yuborganlar soni
    long countByTopshiriqIdAndHolatiNot(Long topshiriqId, TopshiriqHolati holati);

    // Aniq bitta holatdagilar soni (masalan BAHOLANDI)
    long countByTopshiriqIdAndHolati(Long topshiriqId, TopshiriqHolati holati);
}