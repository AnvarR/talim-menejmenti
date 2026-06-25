package com.edu.talim.dto;

import lombok.Data;

@Data
public class FanCreateDTO {

    // Kafedra ID si (select orqali tanlanadi)
    private Long kafedraId;

    // Fan nomi (qo'lda kiritiladi)
    private String fanNomi;
}