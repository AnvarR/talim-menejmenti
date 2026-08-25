package com.edu.talim.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class JavobCreateDTO {
    private UUID savolId;
    private String authorId;
    private String authorType; // "USER" yoki "STUDENT"
    private String mazmun;
}