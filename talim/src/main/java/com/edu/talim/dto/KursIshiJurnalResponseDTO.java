package com.edu.talim.dto;

import java.util.UUID;

import com.edu.talim.entity.enums.Semestr;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KursIshiJurnalResponseDTO {

    private UUID oqituvchiFanTaqsimlashId;
    private String fanNomi;
    private String oqituvchiFio;
    private String kursNomi;
    private String guruhNomi;
    private String oquvYiliNomi;
    private Semestr semestr;

    // Har bir ustun — bitta kurs ishi (mavzu + umumiy muddat)
    private List<KursIshiUstunDTO> kursIshlari;

    private List<KursantKursIshiDTO> kursantlar;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class KursIshiUstunDTO {
        private Long kursIshiId;
        private String mavzuNomi;
        private LocalDate muddat;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class KursantKursIshiDTO {
        private UUID studentId;
        private String studentFio;

        // Har bir kursIshlari ustuniga mos baho (bir xil tartibda)
        private List<BahoDTO> baholar;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class BahoDTO {
        private Long kursIshiBahoId;
        private Integer baho;
        private Integer qaytaTopshirishBaho;
    }
}