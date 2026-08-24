package com.edu.talim.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavolResponseDTO {
    private Long id;
    private String authorId;
    private String authorType;
    private String authorFio;
    private String authorPhoto;
    private String mavzu;
    private String mazmun;
    private String faylUrl;
    private Integer javoblarSoni;
    private Integer korishlarSoni;
    private String createdAt;
}