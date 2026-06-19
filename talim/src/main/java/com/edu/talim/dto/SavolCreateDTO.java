package com.edu.talim.dto;

import lombok.Data;

@Data
public class SavolCreateDTO {
    private Long authorId;
    private String authorType; // "USER" yoki "STUDENT"
    private String mavzu;
    private String mazmun;
    private String faylUrl;
}