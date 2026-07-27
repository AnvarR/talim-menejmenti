package com.edu.talim.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MavzuDTO {
    private Long darsJurnaliId;
    private LocalDate sana;
    private String mavzuNomi;
}