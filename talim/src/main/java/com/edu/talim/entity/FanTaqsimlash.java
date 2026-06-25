package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fan_taqsimlash")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FanTaqsimlash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qaysi fan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fan_id", nullable = false)
    private Fan fan;

    // Qaysi kurs
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kurs_id", nullable = false)
    private Course kurs;

    // Qaysi guruh
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guruh_id", nullable = false)
    private Group guruh;

    // Umumiy soat hajmi
    @Column(nullable = false)
    private Integer soatHajmi;

    // Ma'ruza soati
    @Column(nullable = false)
    private Integer marruzaSoati;

    // Seminar (Amaliy) soati
    @Column(nullable = false)
    private Integer seminarSoati;

    // Mustaqil ta'lim soati
    @Column(nullable = false)
    private Integer mustaqilTalimSoati;

    // Amaliyot mavjudligi
    @Column(nullable = false)
    private Boolean amaliyotMavjud;

    // Kurs ishi mavjudligi
    @Column(nullable = false)
    private Boolean kursIshiMavjud;
}