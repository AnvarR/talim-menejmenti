package com.edu.talim.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ReytingDaftarchasiDTO {

    private Long studentId;
    private String studentFio;
    private Integer globalSemestr; // 1..8
    private Integer kursRaqami;

    private List<FanNatijaDTO> fanlar;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class FanNatijaDTO {
        private String fanNomi;
        private Integer soatHajmi;
        private String oqituvchiFio;
        private Double semestrBahosi; // R(SEM)
    }
}