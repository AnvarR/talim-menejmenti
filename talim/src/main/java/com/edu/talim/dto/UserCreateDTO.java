package com.edu.talim.dto;

import lombok.Data;

@Data
public class UserCreateDTO {

    // Umumiy ma'lumot
    private String jshshir;
    private String passportMalumotlari;
    private String tugilganSana;        // "12.01.2024" yoki "2024-01-12"
    private String fuqaroligi;
    private String fio;
    private String hujjatBerilganSana;  // "12.01.2024" yoki "2024-01-12"
    private String millati;
    private String telefon1;
    private String telefon2;
    private String malumoti;
    private String jinsi;
    private String hujjatBerganTashkilot;
    private String pochtaManzili;

    // Ish joyi va lavozimi
    private Long tarkibiyTuzilmaId;
    private String lavozimi;
    private String ilmiyUnvoni;
    private String ilmiyDarajasi;
    private String guvohnomaNomeri;
    private String harbiyUnvoni;
    private String foydalanuvchiRoli;
    private String username;
}