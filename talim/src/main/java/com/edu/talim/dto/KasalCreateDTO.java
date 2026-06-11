package com.edu.talim.dto;

import lombok.Data;

@Data
public class KasalCreateDTO {
    private Long studentId;
    private String kasallikSababi;
    private String kiritilganSana;    // DD.MM.YYYY
    private String murojaatvaqti;     // HH:MM:SS
    private String kasalYuborilganJoy; // POLIKLINIKA, GOSPITAL...
    private String mutaxassisTuri;
    private String boshlanishSanasi;  // DD.MM.YYYY
    private String tugashSanasi;      // DD.MM.YYYY
}