package com.edu.talim.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    @NotBlank(message = "Hozirgi parol kiritilishi shart")
    private String hozirgiParol;

    @NotBlank(message = "Yangi parol kiritilishi shart")
    @Size(min = 6, message = "Yangi parol kamida 6 belgidan iborat bo'lishi kerak")
    private String yangiParol;

    @NotBlank(message = "Yangi parolni takror kiritish shart")
    private String yangiParolTakror;
}