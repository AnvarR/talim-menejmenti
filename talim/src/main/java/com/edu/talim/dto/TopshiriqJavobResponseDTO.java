package com.edu.talim.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TopshiriqJavobResponseDTO {

    private Long id;
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