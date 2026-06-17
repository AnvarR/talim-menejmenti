package com.edu.talim.dto;

import lombok.*;

/**
 * Fan ma'lumotlarini frontendga qaytarish uchun DTO.
 * Entity dagi barcha kerakli ma'lumotlar shu DTO orqali yuboriladi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FanResponseDTO {

    /** Fan ID si */
    private Long id;

    /** Kafedra ID si */
    private Long kafedraId;

    /** Kafedra nomi */
    private String kafedraNomi;

    /**
     * Kafedra boshlig'i F.I.SH.
     * Tanlangan kafedra bo'yicha bazadan avtomatik olinadi.
     * Foydalanuvchi (User) ning lavozimi "Kafedra boshlig'i" bo'lgan
     * va shu kafedraga tegishli xodim topiladi.
     */
    private String kafedraBoshligiFio;

    /** Fan nomi */
    private String fanNomi;

    /** Umumiy soat hajmi */
    private Integer soatHajmi;

    /** Kurs ID si */
    private Long kursId;

    /** Kurs raqami (masalan: "3-kurs") */
    private String kursRaqami;

    /** Guruh ID si */
    private Long guruhId;

    /** Guruh nomi (masalan: "BI-323") */
    private String guruhNomi;

    /** Ma'ruza soatlari soni */
    private Integer marruzaSoati;

    /** Seminar (Amaliy) / Lab soatlari soni */
    private Integer seminarSoati;

    /** Mustaqil ta'lim soatlari soni */
    private Integer mustaqilTalimSoati;

    /** Amaliyot mavjudligi */
    private Boolean amaliyotMavjud;

    /** Kurs ishi mavjudligi */
    private Boolean kursIshiMavjud;

    /** Yaratilgan vaqt */
    private String createdAt;
}