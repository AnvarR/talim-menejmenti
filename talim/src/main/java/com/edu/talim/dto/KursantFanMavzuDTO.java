package com.edu.talim.dto;

import java.util.UUID;

import com.edu.talim.entity.enums.Semestr;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KursantFanMavzuDTO {
    private UUID darsJurnaliId;
    private String fanNomi;
    private String mavzuNomi;
    private Long topshiriqlarSoni;
    private Semestr semestr;
    private String oquvYiliNomi;
}