package com.edu.talim.dto;

import lombok.Data;

@Data
public class StudentCreateDTO {

    private String photoUrl;
    private String jshshir;
    private String passportSeria;
    private String hujjatBerilganSana;  // "12.01.2024" yoki "2024-01-12"
    private String tugilganSana;         // "12.01.2024" yoki "2024-01-12"
    private String fuqaroligi;
    private String fio;
    private String millati;
    private String telefon1;
    private String telefon2;
    private String email1;
    private String email2;
    private String malumoti;
    private String jinsi;
    private String hujjatBerganTashkilot;
    private String pochtaManzili;
    private String harbiyUnvoni;
    private String guvohnomaNomeri;
    private String kursi;               // "1-kurs", "2-kurs", "3-kurs", "4-kurs"
    private String guruhi;              // "125-guruh"
    private String lavozimi;
    private String type;                // "KURSANT" yoki "TINGLOVCHI"
}