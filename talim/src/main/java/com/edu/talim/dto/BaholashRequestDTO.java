package com.edu.talim.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BaholashRequestDTO {
    private Integer baho;
    private String baholashSharhi;
}