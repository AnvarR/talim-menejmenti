package com.edu.talim.dto;

import com.edu.talim.entity.enums.Semestr;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TopshiriqHolatiSahifaDTO {

    private List<TopshiriqYuborishHolatiDTO> qatorlar;
    private MalumotDTO malumot;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class MalumotDTO {
        private String oquvYiliNomi;
        private Semestr semestr;
        private String fanNomi;
        private String mashgulotNomi; // masalan "Mustaqil ta'lim"
        private String mavzuNomi;
    }
}