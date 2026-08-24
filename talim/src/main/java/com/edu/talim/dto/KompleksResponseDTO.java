package com.edu.talim.dto;

import java.util.UUID;

import com.edu.talim.entity.enums.MaterialKategoriyasi;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KompleksResponseDTO {

    private Long id;
    private UUID oqituvchiFanTaqsimlashId;
    private String fanNomi;
    private String oqituvchiFISH;
    private String kursNomi;
    private Long oquvYiliId;
    private String oquvYiliNomi;
    private String materialNomi;
    private MaterialKategoriyasi materialKategoriyasi;
    private LocalDateTime biriktirilganVaqt;
    private List<KompleksFaylDTO> fayllar;
}