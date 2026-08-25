package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KompleksFaylDTO {
    private UUID id;
    private String faylNomi;
    private String faylUrl;
    private String faylTuri;
}