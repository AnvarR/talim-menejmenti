package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavolResponseDTO {
    private UUID id;
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