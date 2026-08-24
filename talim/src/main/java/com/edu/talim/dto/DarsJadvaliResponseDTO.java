package com.edu.talim.dto;

import java.util.UUID;

import com.edu.talim.entity.enums.HaftaKuni;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DarsJadvaliResponseDTO {

    private Long id;
    private UUID kursId;
    private String kursNomi;
    private Long oquvYiliId;
    private String oquvYiliNomi;
    private HaftaKuni haftaKuni;
    private String faylNomi;
    private String faylUrl;
    private String faylTuri;
}