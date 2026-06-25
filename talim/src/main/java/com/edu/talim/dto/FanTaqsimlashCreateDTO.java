package com.edu.talim.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FanTaqsimlashCreateDTO {

    // Fan ID si
    private Long fanId;

    // Kurs ID si
    private Long kursId;

    // Guruh ID si
    private Long guruhId;

    // Umumiy soat hajmi
    private Integer soatHajmi;

    // Ma'ruza soati
    private Integer marruzaSoati;

    // Seminar (Amaliy) soati
    private Integer seminarSoati;

    // Mustaqil ta'lim soati
    private Integer mustaqilTalimSoati;

    // Amaliyot mavjudligi
    private Boolean amaliyotMavjud;

    // Kurs ishi mavjudligi
    private Boolean kursIshiMavjud;
}