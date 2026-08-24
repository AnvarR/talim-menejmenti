package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FanTaqsimlashResponseDTO {

    private UUID id;

    // Fan ma'lumotlari
    private UUID fanId;
    private String fanNomi;

    // Kafedra ma'lumotlari
    private UUID kafedraId;
    private String kafedraNomi;

    // Kurs ma'lumotlari
    private UUID kursId;
    private String kursRaqami;

    // Guruh ma'lumotlari
    private UUID guruhId;
    private String guruhNomi;

    // Soat ma'lumotlari
    private Integer soatHajmi;
    private Integer marruzaSoati;
    private Integer seminarSoati;
    private Integer mustaqilTalimSoati;

    // Qo'shimcha
    private Boolean amaliyotMavjud;
    private Boolean kursIshiMavjud;
}