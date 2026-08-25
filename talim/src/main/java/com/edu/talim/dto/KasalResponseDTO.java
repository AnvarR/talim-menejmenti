package com.edu.talim.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KasalResponseDTO {
    private UUID id;

    // Kursant ma'lumotlari
    private UUID studentId;
    private String kurs;
    private String guruh;
    private String fio;
    private String passportMalumotlari;
    private String jshshir;
    private String tugilganSana;
    private String jinsi;
    private String photoUrl;

    // Kasallik ma'lumotlari
    private String kasallikSababi;
    private String kiritilganSana;
    private String murojaatvaqti;
    private String kasalYuborilganJoy;
    private String mutaxassisTuri;
    private String boshlanishSanasi;
    private String tugashSanasi;
    private Long muddat;

    private String createdAt;
}