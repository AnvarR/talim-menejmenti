package com.edu.talim.dto;

import lombok.Data;

@Data
public class XabarCreateDTO {
    private Long senderId;
    private String senderType;   // "USER" yoki "STUDENT"
    private Long receiverId;
    private String receiverType; // "USER" yoki "STUDENT"
    private String mavzu;
    private String mazmun;
}