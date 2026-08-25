package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "amaliyotlar")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Amaliyot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oqituvchi_fan_taqsimlash_id", nullable = false)
    private OqituvchiFanTaqsimlash oqituvchiFanTaqsimlash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oquv_yili_id", nullable = false)
    private OquvYili oquvYili;

    // Amaliyot tugash sanasi - shu kunga barchaga baho qo'yiladi (semestrga ajratilmaydi)
    @Column(nullable = false)
    private LocalDate tugashSanasi;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime yaratilganVaqt = LocalDateTime.now();

    @OneToMany(mappedBy = "amaliyot", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AmaliyotBaho> baholar = new ArrayList<>();
}