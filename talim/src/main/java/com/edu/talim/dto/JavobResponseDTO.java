package com.edu.talim.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JavobResponseDTO {
    private Long id;
    private Long savolId;
    private Long authorId;
    private String authorType;
    private String authorFio;   // javob beruvchining F.I.SH
    private String authorPhoto; // javob beruvchining rasmi
    private String mazmun;
    private String createdAt;
}