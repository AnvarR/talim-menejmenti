package com.edu.talim.dto;

import lombok.Data;

@Data
public class JavobCreateDTO {
    private Long savolId;
    private String authorId;
    private String authorType; // "USER" yoki "STUDENT"
    private String mazmun;
}