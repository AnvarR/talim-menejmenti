package com.edu.talim.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavolStatistikaDTO {

    // Yuqoridagi 4 ta karta uchun
    private long barchaSavollar;      // BARCHA SAVOLLAR kartasi
    private long barchaJavoblar;      // BARCHA BERILGAN JAVOBLAR kartasi
    private long barchaKorishlar;     // BARCHA KO'RILGANLAR kartasi (barcha savollarning korishlarSoni yig'indisi)
    private long yuklananMateriallar; // YUKLANGAN MATERIALLAR kartasi (faylUrl mavjud savollar soni)
}