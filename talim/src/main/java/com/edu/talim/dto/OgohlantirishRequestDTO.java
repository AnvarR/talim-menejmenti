package com.edu.talim.dto;

import com.edu.talim.entity.enums.Semestr;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OgohlantirishRequestDTO {
    private Long senderId;      // ogohlantirishni yuborayotgan xodim (fakultet boshlig'i) ID si
    private Long oquvYiliId;
    private Semestr semestr;
    private Long kursId;        // ixtiyoriy
    private Long guruhId;       // ixtiyoriy
    private Long fanId;         // ixtiyoriy
}