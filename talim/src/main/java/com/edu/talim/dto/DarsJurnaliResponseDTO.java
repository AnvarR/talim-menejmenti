package com.edu.talim.dto;

import com.edu.talim.entity.enums.DarsTuri;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DarsJurnaliResponseDTO {

    private Long id;
    private Long oqituvchiFanTaqsimlashId;
    private String fanNomi;
    private String oqituvchiFio;
    private String kursNomi;
    private String guruhNomi;
    private Long oquvYiliId;
    private String oquvYiliNomi;
    private DarsTuri darsTuri;
    private LocalDate sana;
    private Integer soat;
    private String mavzuNomi;
    private String topshiriqFaylNomi;
    private String topshiriqFaylUrl;

    // Shu darsdagi barcha kursantlar davomati
    private List<DavomatResponseDTO> davomatlar;
}