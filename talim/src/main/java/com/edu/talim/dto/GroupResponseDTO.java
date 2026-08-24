package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GroupResponseDTO {
    private UUID id;
    private String guruhNomi;
    private UUID kursId;
    private String kursNomi;
    private Long kursantSoni;
}