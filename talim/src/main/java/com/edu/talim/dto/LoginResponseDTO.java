package com.edu.talim.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
    private Long id;
    private String fio;
    private String role;
    private String username;
    private String photoUrl;
    private String token;
}