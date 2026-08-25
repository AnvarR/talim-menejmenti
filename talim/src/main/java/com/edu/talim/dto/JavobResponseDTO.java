package com.edu.talim.dto;

import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JavobResponseDTO {
    private UUID id;
    private UUID savolId;
    private String authorId;
    private String authorType;
    private String authorFio;   // javob beruvchining F.I.SH
    private String authorPhoto; // javob beruvchining rasmi
    private String mazmun;
    private String createdAt;
}