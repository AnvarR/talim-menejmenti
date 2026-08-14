package com.edu.talim.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GuruhHisobotSatriDTO {
    private String kursNomi;
    private String guruhNomi;
    private Double joriyBaho;
    private Double oraliqBaho;
    private Double yakuniyBaho;
    private Double semestrBahosi;
    private String ozlashtirishDarajasi;
}