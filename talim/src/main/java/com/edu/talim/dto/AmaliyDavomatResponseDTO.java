package com.edu.talim.dto;

import java.util.UUID;

import com.edu.talim.entity.enums.DavomatHolati;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AmaliyDavomatResponseDTO {

    private UUID id;
    private UUID studentId;
    private String studentFio;
    private DavomatHolati holat;
    private Integer baho;
    private Integer qaytaTopshirishBaho;
    private Boolean bloklanganMi;
}