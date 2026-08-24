package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MavzuDTO {
    private UUID darsJurnaliId;
    private LocalDate sana;
    private String mavzuNomi;
}