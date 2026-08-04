package com.edu.talim.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AmaliyotJurnalResponseDTO {

    private Long oqituvchiFanTaqsimlashId;
    private String fanNomi;
    private String oqituvchiFio;
    private String kursNomi;
    private String guruhNomi;
    private String oquvYiliNomi;

    // Har bir ustun — bitta amaliyot (tugash sanasi)
    private List<AmaliyotUstunDTO> amaliyotlar;

    private List<KursantAmaliyotDTO> kursantlar;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class AmaliyotUstunDTO {
        private Long amaliyotId;
        private LocalDate tugashSanasi;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class KursantAmaliyotDTO {
        private Long studentId;
        private String studentFio;

        // Har bir amaliyotlar ustuniga mos baho (bir xil tartibda)
        private List<BahoDTO> baholar;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class BahoDTO {
        private Long amaliyotBahoId;
        private Integer baho;
        private Integer qaytaTopshirishBaho;
    }
}