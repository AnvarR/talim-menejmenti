package com.edu.talim.dto;

import com.edu.talim.entity.enums.Semestr;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KursantFanMavzuDTO {
    private Long darsJurnaliId;
    private String fanNomi;
    private String mavzuNomi;
    private Long topshiriqlarSoni;
    private Semestr semestr;
    private String oquvYiliNomi;
}