package com.edu.talim.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Login kiritilishi shart")
    private String username;

    @NotBlank(message = "Parol kiritilishi shart")
    private String password;
}