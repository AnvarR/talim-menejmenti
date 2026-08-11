package com.edu.talim.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OqituvchiFanTaqsimlashResponseDTO {

    private Long id;

    // Fan ma'lumotlari
    private Long fanTaqsimlashId;
    private String fanNomi;
    private String kafedraNomi;

    // O'qituvchi ma'lumotlari
    private Long oqituvchiId;
    private String oqituvchiFio;

    // Dars turi
    private String darsTuri;

    // Soat hajmi (dars turidan avtomatik)
    private Integer soatHajmi;

    // Kurs
    private Long kursId;
    private String kursRaqami;

    // Guruhlar
    private List<String> guruhlar;

    private Boolean oraliqNazoratRuxsat;
    private Boolean yakuniyNazoratRuxsat;
}