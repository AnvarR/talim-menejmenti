package com.edu.talim.dto;

import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserCreateDTO {

    // Umumiy ma'lumot
    @NotBlank(message = "JSHSHIR kiritilishi shart")
    @Size(min = 14, max = 14, message = "JSHSHIR 14 ta raqamdan iborat bo'lishi kerak")
    private String jshshir;

    @NotBlank(message = "Pasport ma'lumotlari kiritilishi shart")
    private String passportMalumotlari;

    @NotBlank(message = "Tug'ilgan sana kiritilishi shart")
    private String tugilganSana;        // "12.01.2024" yoki "2024-01-12"

    private String fuqaroligi;

    @NotBlank(message = "F.I.O kiritilishi shart")
    private String fio;

    @NotBlank(message = "Hujjat berilgan sana kiritilishi shart")
    private String hujjatBerilganSana;  // "12.01.2024" yoki "2024-01-12"

    private String millati;
    private String telefon1;
    private String telefon2;
    private String malumoti;
    private String jinsi;
    private String hujjatBerganTashkilot;

    @Email(message = "Email formati noto'g'ri")
    private String pochtaManzili;

    // Ish joyi va lavozimi
    private UUID tarkibiyTuzilmaId;
    private String lavozimi;
    private String ilmiyUnvoni;
    private String ilmiyDarajasi;
    private String guvohnomaNomeri;
    private String harbiyUnvoni;
    private String foydalanuvchiRoli;
    private String username;
}