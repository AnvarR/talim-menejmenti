package com.edu.talim.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class SutkalikNaryadCreateDTO {
    private UUID studentId;
    private String xizmatOtashJoyi;
    private String qabulQilishSanasi;
    private String topshirishSanasi;
    private String oquvYili;
}