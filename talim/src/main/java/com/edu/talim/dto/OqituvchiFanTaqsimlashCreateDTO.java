package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OqituvchiFanTaqsimlashCreateDTO {

    // O'quv bo'limi taqsimlagan fan ID si
    private UUID fanTaqsimlashId;

    // O'qituvchi ID si
    private UUID oqituvchiId;

    // Dars turi: "MARUZA", "SEMINAR", "MUSTAQIL_TALIM"
    private String darsTuri;

    // Kurs ID si
    private UUID kursId;

    // Guruhlar ID lari (bir nechta)
    private List<UUID> guruhIds;

    // Faqat "Kurs ishi" uchun qo'lda kiritiladi (boshqa turlar uchun avtomatik hisoblanadi)
    private Integer soatHajmi;
}