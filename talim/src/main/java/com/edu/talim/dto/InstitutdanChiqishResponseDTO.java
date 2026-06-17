package com.edu.talim.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutdanChiqishResponseDTO {
    private Long id;
    private Long studentId;
    private String fio;
    private String kurs;
    private String guruh;
    private String photoUrl;
    private String chiqishSababi;
    private String izoh;
    private String chiqganSana;
    private String chiqganVaqt;
    private String qaytganSana;
    private String qaytganVaqt;
    private String oquvYili;
    private String createdAt;
}