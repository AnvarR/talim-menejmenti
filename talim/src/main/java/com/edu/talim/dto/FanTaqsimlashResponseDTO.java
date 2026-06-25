package com.edu.talim.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FanTaqsimlashResponseDTO {

    private Long id;

    // Fan ma'lumotlari
    private Long fanId;
    private String fanNomi;

    // Kafedra ma'lumotlari
    private Long kafedraId;
    private String kafedraNomi;

    // Kurs ma'lumotlari
    private Long kursId;
    private String kursRaqami;

    // Guruh ma'lumotlari
    private Long guruhId;
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