package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KochirishKursantDTO {
    private UUID studentId;
    private String fio;
    private String joriyKursNomi;   // masalan "2-kurs"
    private String guruhNomi;
    // "RUXSAT" - shu o'quv yilida allaqachon keyingi kursga o'tkazilgan
    // "TASDIQLASH" - hali o'tkazilmagan, kutilmoqda
    private String holati;
}