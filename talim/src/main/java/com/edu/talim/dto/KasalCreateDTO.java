package com.edu.talim.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class KasalCreateDTO {
    private UUID studentId;
    private String kasallikSababi;
    private String kiritilganSana;    // DD.MM.YYYY
    private String murojaatvaqti;     // HH:MM:SS
    private String kasalYuborilganJoy; // POLIKLINIKA, GOSPITAL...
    private String mutaxassisTuri;
    private String boshlanishSanasi;  // DD.MM.YYYY
    private String tugashSanasi;      // DD.MM.YYYY
}