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

        // ===== 1-ORALIQ =====
        // R(KB) — 1-oraliq kesim sanasigacha bo'lgan kunlik baholar o'rtachasi
        private Double rkb1;
        // R(ON) — 1-oraliq nazorat bahosi (qo'lda)
        private Integer ron1;
        private Integer ron1QaytaTopshirishBaho;
        // R(MT) — 1-oraliq mustaqil ta'lim (hozircha null)
        private Double rmt1;
        // R(1ON) = (R(KB1)+R(ON1)+R(MT1))/3
        private Double r1on;
        // 1-oraliqning kesim sanasi (frontendda ko'rsatish uchun)
        private LocalDate kesim1Sanasi;

        // ===== 2-ORALIQ =====
        // R(KB) — 1-oraliq kesim sanasidan 2-oraliq kesim sanasigacha bo'lgan kunlik baholar o'rtachasi
        private Double rkb2;
        // R(ON) — 2-oraliq nazorat bahosi (qo'lda)
        private Integer ron2;
        private Integer ron2QaytaTopshirishBaho;
        // R(MT) — 2-oraliq mustaqil ta'lim (hozircha null)
        private Double rmt2;
        // R(2ON) = (R(KB2)+R(ON2)+R(MT2))/3
        private Double r2on;
        // 2-oraliqning kesim sanasi (frontendda ko'rsatish uchun)
        private LocalDate kesim2Sanasi;

        // R(ON.SEM) — (R(1ON)+R(2ON))/2
        private Double ronSem;

        // R(YN) — yakuniy nazorat bahosi (qo'lda)
        private Integer ryn;
        private Integer rynQaytaTopshirishBaho;

        // R(SEM) — (R(ON.SEM)+R(YN))/2
        private Double rsem;
    }
}