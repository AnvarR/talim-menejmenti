package com.edu.talim.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OquvYiliDTO {

    private Long id;
    private String nom;
    private Integer boshlanishYil;
    private Integer tugashYil;
    private Boolean faol;
    private Boolean qoshimchaTahrirRuxsati;
}