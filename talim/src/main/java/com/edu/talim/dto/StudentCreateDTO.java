package com.edu.talim.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentCreateDTO {

    //Umumiy ma'lumot
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

    // Ish joyi
    private String harbiyUnvoni;
    private String guvohnomaNomeri;
    private Long courseId;
    private Long groupId;
    private String lavozimi;

    // KURSANT yoki TINGLOVCHI
    private String type;

}
