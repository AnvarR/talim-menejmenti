package com.edu.talim.dto;

import java.util.UUID;

import com.edu.talim.entity.enums.DavomatHolati;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DavomatResponseDTO {

    private Long id;
    private UUID studentId;
    private String studentFio;
    private DavomatHolati holat;
    private Boolean bloklanganMi;
}