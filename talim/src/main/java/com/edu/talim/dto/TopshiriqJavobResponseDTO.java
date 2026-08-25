package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TopshiriqJavobResponseDTO {

    private UUID id;
    private String izoh;
    private String faylNomi;
    private String faylUrl;
    private LocalDateTime berilganSana;

    private Integer baho;
    private String baholashSharhi;
    private LocalDateTime baholanganSana;

    private Boolean qaytarilganMi;
    private String qaytarishSababi;
}