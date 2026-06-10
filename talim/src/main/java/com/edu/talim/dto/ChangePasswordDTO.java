package com.edu.talim.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String hozirgiParol;
    private String yangiParol;
    private String yangiParolTakror;
}