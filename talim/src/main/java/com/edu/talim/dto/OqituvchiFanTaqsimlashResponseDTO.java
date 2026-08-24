package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OqituvchiFanTaqsimlashResponseDTO {

    private UUID id;

    // Fan ma'lumotlari
    private UUID fanTaqsimlashId;
    private String fanNomi;
    private String kafedraNomi;

    // O'qituvchi ma'lumotlari
    private UUID oqituvchiId;
    private String oqituvchiFio;

    // Dars turi
    private String darsTuri;

    // Soat hajmi (dars turidan avtomatik)
    private Integer soatHajmi;

    // Kurs
    private UUID kursId;
    private String kursRaqami;

    // Guruhlar
    private List<String> guruhlar;

    private Boolean oraliqNazoratRuxsat;
    private Boolean yakuniyNazoratRuxsat;
}