package com.edu.talim.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class InstitutdanChiqishCreateDTO {
    private UUID studentId;
    private String chiqishSababi;
    private String izoh;
    private String chiqganSana;
    private String chiqganVaqt;
    private String qaytganSana;
    private String qaytganVaqt;
    private String oquvYili;
}