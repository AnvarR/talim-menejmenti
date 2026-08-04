package com.edu.talim.dto;

import com.edu.talim.entity.enums.TopshiriqHolati;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KursantTopshiriqDTO {

    private Long topshiriqYuborishId;
    private Long topshiriqId;

    private String nomi;
    private String topshiriqTuri;
    private Integer fayllarSoni;
    private LocalDateTime muddat;

    private TopshiriqHolati holati;

    // Baholangan bo'lsa
    private Integer oxirgiBaho;
    private String baholashSharhi;

    // Qaytarilgan bo'lsa
    private String qaytarishSababi;
}