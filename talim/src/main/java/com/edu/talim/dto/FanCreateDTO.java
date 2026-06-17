package com.edu.talim.dto;

import lombok.Data;

/**
 * Fan qo'shish va tahrirlash uchun DTO.
 * Frontend dan keluvchi ma'lumotlar shu DTO orqali qabul qilinadi.
 */
@Data
public class FanCreateDTO {

    /** Kafedra ID si (TarkibiyTuzilma dan tanlanadi) */
    private Long kafedraId;

    /** Fan nomi (qo'lda kiritiladi) */
    private String fanNomi;

    /** Umumiy soat hajmi */
    private Integer soatHajmi;

    /** Kurs ID si */
    private Long kursId;

    /** Guruh ID si */
    private Long guruhId;

    /** Ma'ruza soatlari soni */
    private Integer marruzaSoati;

    /** Seminar (Amaliy) / Lab soatlari soni */
    private Integer seminarSoati;

    /** Mustaqil ta'lim soatlari soni */
    private Integer mustaqilTalimSoati;

    /** Amaliyot mavjudligi (true/false) */
    private Boolean amaliyotMavjud;

    /** Kurs ishi mavjudligi (true/false) */
    private Boolean kursIshiMavjud;
}