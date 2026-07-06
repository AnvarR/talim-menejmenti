package com.edu.talim.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KompleksFaylDTO {
    private Long id;
    private String faylNomi;
    private String faylUrl;
    private String faylTuri;
}