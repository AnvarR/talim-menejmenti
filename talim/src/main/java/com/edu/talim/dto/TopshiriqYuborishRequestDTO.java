package com.edu.talim.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TopshiriqYuborishRequestDTO {

    private Long topshiriqId;
    private List<Item> kursantlar;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class Item {
        private Long studentId;
        private LocalDateTime muddat;
    }
}