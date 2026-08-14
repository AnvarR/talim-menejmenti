package com.edu.talim.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class StudentDetailDTO {

    private Long id;
    private String photoUrl;
    private String jshshir;
    private String fio;
    private String malumoti;
    private String passportSeria;
    private LocalDate hujjatBerilganSana;
    private String jinsi;
    private LocalDate tugilganSana;
    private String millati;
    private String hujjatBerganTashkilot;
    private String fuqaroligi;
    private String telefon1;
    private String telefon2;
    private String email1;
    private String email2;
    private String harbiyUnvoni;
    private String guvohnomaNomeri;
    private String kursi;
    private String guruhi;
    private String lavozimi;
    private String type;
    private String oquvYili;
    private LocalDateTime createdAt;
}