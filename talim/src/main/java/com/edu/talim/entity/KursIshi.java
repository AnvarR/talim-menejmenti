package com.edu.talim.entity;

import com.edu.talim.entity.enums.Semestr;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kurs_ishlari")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KursIshi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oqituvchi_fan_taqsimlash_id", nullable = false)
    private OqituvchiFanTaqsimlash oqituvchiFanTaqsimlash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oquv_yili_id", nullable = false)
    private OquvYili oquvYili;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private Semestr semestr;

    // Kurs ishi mavzusi/nomi
    @Column(nullable = false, length = 255)
    private String mavzuNomi;

    // Barcha kursantlar uchun umumiy topshirish muddati - shu kunga barchaga baho qo'yiladi
    @Column(nullable = false)
    private LocalDate muddat;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime yaratilganVaqt = LocalDateTime.now();

    @OneToMany(mappedBy = "kursIshi", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<KursIshiBaho> baholar = new ArrayList<>();
}