package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "topshiriq_javoblar")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TopshiriqJavob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topshiriq_yuborish_id", nullable = false)
    private TopshiriqYuborish topshiriqYuborish;

    // Kursantning izohi
    @Column(columnDefinition = "TEXT")
    private String izoh;

    // Kursant yuklagan fayl
    private String faylNomi;
    private String faylYoli;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime berilganSana = LocalDateTime.now();

    // Baholash (o'qituvchi tomonidan, null bo'lsa hali baholanmagan)
    private Integer baho;

    @Column(columnDefinition = "TEXT")
    private String baholashSharhi;

    private LocalDateTime baholanganSana;

    // Baholamasdan qayta topshirish uchun qaytarilgan bo'lsa
    @Builder.Default
    private Boolean qaytarilganMi = false;

    @Column(columnDefinition = "TEXT")
    private String qaytarishSababi;
}