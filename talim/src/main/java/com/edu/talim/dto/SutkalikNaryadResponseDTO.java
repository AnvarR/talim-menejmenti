package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SutkalikNaryadResponseDTO {
    private Long id;
    private UUID studentId;
    private String fio;
    private String kurs;
    private String guruh;
    private String photoUrl;
    private String xizmatOtashJoyi;
    private String qabulQilishSanasi;
    private String topshirishSanasi;
    private String oquvYili;
    private String createdAt;
}