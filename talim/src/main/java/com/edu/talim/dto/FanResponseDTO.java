package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FanResponseDTO {

    // Fan ID si
    private UUID id;

    // Kafedra ID si
    private UUID kafedraId;

    // Kafedra nomi
    private String kafedraNomi;

    // Kafedra boshlig'i F.I.SH — avtomatik aniqlanadi
    private String kafedraBoshligiFio;

    // Fan nomi
    private String fanNomi;
}