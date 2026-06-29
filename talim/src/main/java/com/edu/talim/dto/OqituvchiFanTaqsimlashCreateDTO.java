package com.edu.talim.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OqituvchiFanTaqsimlashCreateDTO {

    // O'quv bo'limi taqsimlagan fan ID si
    private Long fanTaqsimlashId;

    // O'qituvchi ID si
    private Long oqituvchiId;

    // Dars turi: "MARUZA", "SEMINAR", "MUSTAQIL_TALIM"
    private String darsTuri;

    // Kurs ID si
    private Long kursId;

    // Guruhlar ID lari (bir nechta)
    private List<Long> guruhIds;
}