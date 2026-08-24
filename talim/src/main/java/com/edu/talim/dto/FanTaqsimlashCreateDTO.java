package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FanTaqsimlashCreateDTO {

    // Fan ID si
    private UUID fanId;

    // Kurs ID si
    private UUID kursId;

    // Guruh ID si
    private UUID guruhId;

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