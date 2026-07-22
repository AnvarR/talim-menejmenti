package com.edu.talim.dto;

import com.edu.talim.entity.enums.DavomatHolati;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DavomatResponseDTO {

    private Long id;
    private Long studentId;
    private String studentFio;
    private DavomatHolati holat;
    private Boolean bloklanganMi;
}