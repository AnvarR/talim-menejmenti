package com.edu.talim.dto;

import java.util.UUID;

import com.edu.talim.entity.enums.TopshiriqHolati;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TopshiriqYuborishHolatiDTO {

    private Long topshiriqYuborishId;
    private UUID studentId;
    private String studentFio;
    private String guruhNomi;

    private String topshiriqTuri;
    private Long topshiriqId;
    private String mavzuNomi;

    private TopshiriqHolati holati;
    private Integer oxirgiBaho;
    private LocalDateTime muddat;
    private LocalDateTime oxirgiHarakatSanasi;
}