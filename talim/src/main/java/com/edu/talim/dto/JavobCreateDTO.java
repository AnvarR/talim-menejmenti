package com.edu.talim.dto;

import lombok.Data;

@Data
public class JavobCreateDTO {
    private Long savolId;
    private Long authorId;
    private String authorType; // "USER" yoki "STUDENT"
    private String mazmun;
}