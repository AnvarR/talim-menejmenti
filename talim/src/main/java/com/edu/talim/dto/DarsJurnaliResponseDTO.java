package com.edu.talim.dto;

import java.util.UUID;

import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.entity.enums.Semestr;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DarsJurnaliResponseDTO {

    private UUID id;
    private UUID oqituvchiFanTaqsimlashId;
    private String fanNomi;
    private String oqituvchiFio;
    private String kursNomi;
    private String guruhNomi;
    private UUID oquvYiliId;
    private String oquvYiliNomi;
    private DarsTuri darsTuri;
    private Semestr semestr;
    private LocalDate sana;
    private Integer soat;
    private String mavzuNomi;
    private String topshiriqFaylNomi;
    private String topshiriqFaylUrl;

    // Shu darsdagi barcha kursantlar davomati
    private List<DavomatResponseDTO> davomatlar;
}