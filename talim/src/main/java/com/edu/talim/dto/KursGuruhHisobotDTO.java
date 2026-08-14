package com.edu.talim.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KursGuruhHisobotDTO {
    private Integer jamiKursantlar;
    private Double ortachaBaho;
    private String engYaxshiGuruh;
    private String nazoratgaOlishKerak; // eng past o'rtacha bahoga ega guruh
    private List<GuruhHisobotSatriDTO> guruhlar;
}