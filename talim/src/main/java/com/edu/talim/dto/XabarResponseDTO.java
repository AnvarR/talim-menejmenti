package com.edu.talim.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XabarResponseDTO {
    private Long id;
    private Long senderId;
    private String senderType;
    private String senderFio;     // yuboruvchining F.I.SH
    private String senderPhoto;   // yuboruvchining rasmi
    private Long receiverId;
    private String receiverType;
    private String receiverFio;   // qabul qiluvchining F.I.SH
    private String receiverPhoto; // qabul qiluvchining rasmi
    private String mavzu;
    private String mazmun;
    private Boolean oqilgan;
    private String createdAt;
}