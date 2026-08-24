package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HisobotSatriDTO {
    private UUID studentId;
    private String studentFio;
    private String reytingDaftarchasiRaqami;
    private String kursNomi;
    private String guruhNomi;
    private String fanNomi;
    private Double joriyBaho;      // R(KB) lar o'rtachasi
    private Double oraliqBaho;     // R(ONSEM)
    private Double yakuniyBaho;    // R(YN) (effektiv, qayta topshirilgan bo'lsa - o'shani)
    private Double semestrBahosi;  // (joriy+oraliq+yakuniy)/3, 1 xonagacha
    private String ozlashtirishDarajasi; // A'lo / Yaxshi / Qoniqarli / Past
}