package com.edu.talim.dto;

import java.util.UUID;

import com.edu.talim.entity.enums.Semestr;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MustaqilTalimJurnalResponseDTO {

    private UUID oqituvchiFanTaqsimlashId;
    private String fanNomi;
    private String oqituvchiFio;
    private String kursNomi;
    private String guruhNomi;
    private String oquvYiliNomi;
    private Semestr semestr;

    // Har bir ustun — bitta topshiriq (muddati = sana ustuni)
    private List<TopshiriqUstunDTO> topshiriqlar;

    private List<KursantMTJurnalDTO> kursantlar;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class TopshiriqUstunDTO {
        private UUID topshiriqId;
        private LocalDateTime muddat;
        private Integer soat;
        private String nomi;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class KursantMTJurnalDTO {
        private UUID studentId;
        private String studentFio;

        // Har bir topshiriq ustuniga mos baho (topshiriqlar ro'yxati bilan bir xil tartibda, yo'q bo'lsa null)
        private List<Integer> baholar;

        private Double rmt1;
        private LocalDateTime kesim1Sanasi;

        private Double rmt2;
        private LocalDateTime kesim2Sanasi;
    }
}