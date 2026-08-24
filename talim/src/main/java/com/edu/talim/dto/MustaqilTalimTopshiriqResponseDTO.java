package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MustaqilTalimTopshiriqResponseDTO {

    private Long id;
    private UUID darsJurnaliId;
    private String mavzuNomi;

    private String topshiriqTuri;
    private String nomi;
    private String izoh;

    private LocalDateTime boshlanishSanasi;
    private LocalDateTime yakunlanishSanasi;
    private Integer urinishlarSoni;

    private Boolean yuborilganMi;
    private LocalDateTime yaratilganVaqt;

    // Fayllar (nomi + yuklab olish url)
    private List<TopshiriqFaylDTO> fayllar;

    // "77/0/0" ko'rinishidagi hisoblar
    private Long jamiYuborilgan;
    private Long javobBerganlar;
    private Long baholanganlar;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class TopshiriqFaylDTO {
        private Long id;
        private String faylNomi;
        private String faylUrl;
    }
}