package com.edu.talim.dto;

import java.util.UUID;

import com.edu.talim.entity.enums.Semestr;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OgohlantirishRequestDTO {
    private Long senderId;      // ogohlantirishni yuborayotgan xodim (fakultet boshlig'i) ID si
    private UUID oquvYiliId;
    private Semestr semestr;
    private UUID kursId;        // ixtiyoriy
    private UUID guruhId;       // ixtiyoriy
    private UUID fanId;         // ixtiyoriy
}