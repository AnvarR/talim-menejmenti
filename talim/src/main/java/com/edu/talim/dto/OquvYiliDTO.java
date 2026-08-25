package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OquvYiliDTO {

    private UUID id;
    private String nom;
    private Integer boshlanishYil;
    private Integer tugashYil;
    private Boolean faol;
    private Boolean qoshimchaTahrirRuxsati;
}