package com.edu.talim.dto;

import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StudentCreateDTO {

    private String photoUrl;

    @NotBlank(message = "JSHSHIR kiritilishi shart")
    @Size(min = 14, max = 14, message = "JSHSHIR 14 ta raqamdan iborat bo'lishi kerak")
    private String jshshir;

    @NotBlank(message = "Pasport seriyasi kiritilishi shart")
    private String passportSeria;

    @NotBlank(message = "Hujjat berilgan sana kiritilishi shart")
    private String hujjatBerilganSana;  // "12.01.2024" yoki "2024-01-12"

    @NotBlank(message = "Tug'ilgan sana kiritilishi shart")
    private String tugilganSana;         // "12.01.2024" yoki "2024-01-12"

    private String fuqaroligi;

    @NotBlank(message = "F.I.O kiritilishi shart")
    private String fio;

    private String millati;

    private String telefon1;
    private String telefon2;

    @Email(message = "Email formati noto'g'ri")
    private String email1;

    @Email(message = "Email formati noto'g'ri")
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

    @NotBlank(message = "Turi (KURSANT/TINGLOVCHI) kiritilishi shart")
    private String type;                // "KURSANT" yoki "TINGLOVCHI"

    private UUID oquvYiliId;            // Ro'yxatga olingan o'quv yili
}