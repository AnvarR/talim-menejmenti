package com.edu.talim.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GroupResponseDTO {
    private Long id;
    private String guruhNomi;
    private Long kursId;
    private String kursNomi;
    private Long kursantSoni;
}