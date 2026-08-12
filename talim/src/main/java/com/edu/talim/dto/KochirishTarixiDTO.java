package com.edu.talim.dto;

import com.edu.talim.entity.enums.KochirishTuri;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KochirishTarixiDTO {
    private Long id;
    private KochirishTuri turi;
    private String eskiKursNomi;
    private String yangiKursNomi;   // CHETLATISH/ARXIVLASH bo'lsa - null
    private String oquvYiliNomi;
    private String sababi;          // faqat CHETLATISH uchun
    private LocalDate sana;
}