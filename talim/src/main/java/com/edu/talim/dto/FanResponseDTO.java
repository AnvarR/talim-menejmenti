package com.edu.talim.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FanResponseDTO {

    // Fan ID si
    private Long id;

    // Kafedra ID si
    private Long kafedraId;

    // Kafedra nomi
    private String kafedraNomi;

    // Kafedra boshlig'i F.I.SH — avtomatik aniqlanadi
    private String kafedraBoshligiFio;

    // Fan nomi
    private String fanNomi;
}