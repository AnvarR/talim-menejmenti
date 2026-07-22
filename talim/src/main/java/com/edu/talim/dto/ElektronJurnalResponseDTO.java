package com.edu.talim.dto;

import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.entity.enums.Semestr;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ElektronJurnalResponseDTO {

    // Fan va o'qituvchi ma'lumotlari
    private Long oqituvchiFanTaqsimlashId;
    private String fanNomi;
    private String oqituvchiFio;
    private String kursNomi;
    private String guruhNomi;
    private String oquvYiliNomi;
    private DarsTuri darsTuri;
    private Semestr semestr;

    // Darslar ro'yxati (sana + davomatlar)
    private List<DarsJurnaliResponseDTO> darslar;

    // Kursantlar ro'yxati (baholar bilan)
    private List<KursantJurnalDTO> kursantlar;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class KursantJurnalDTO {
        private Long studentId;
        private String studentFio;

        // Har bir dars uchun davomat/baho
        private List<AmaliyDavomatResponseDTO> davomatlar;

        // R(KB) — kunlik baholar o'rtachasi (avtomatik)
        private Double rkb;

        // R(ON) — oraliq nazorat bahosi (qo'lda)
        private Integer ron;

        // R(MT) — mustaqil ta'lim (hozircha null)
        private Double rmt;

        // R(1ON) yoki R(2ON) — (R(KB)+R(ON)+R(MT))/3
        private Double r1on;

        // R(ON.SEM) — (R(1ON)+R(2ON))/2 (faqat 2-semestr uchun)
        private Double ronSem;

        // R(YN) — yakuniy nazorat bahosi (qo'lda)
        private Integer ryn;

        // R(SEM) — (R(ON.SEM)+R(YN))/2
        private Double rsem;
    }
}