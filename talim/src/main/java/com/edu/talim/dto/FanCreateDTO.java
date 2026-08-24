package com.edu.talim.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class FanCreateDTO {

    // Kafedra ID si (select orqali tanlanadi)
    private UUID kafedraId;

    // Fan nomi (qo'lda kiritiladi)
    private String fanNomi;
}