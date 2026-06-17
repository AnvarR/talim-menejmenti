package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fanlar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kafedra_id", nullable = false)
    private TarkibiyTuzilma kafedra;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String fanNomi;

    @Column(nullable = false)
    private Integer soatHajmi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kurs_id", nullable = false)
    private Course kurs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guruh_id", nullable = false)
    private Group guruh;

    @Column(nullable = false)
    private Integer marruzaSoati;

    @Column(nullable = false)
    private Integer seminarSoati;

    @Column(nullable = false)
    private Integer mustaqilTalimSoati;

    @Column(nullable = false)
    private Boolean amaliyotMavjud;

    @Column(nullable = false)
    private Boolean kursIshiMavjud;

    @CreationTimestamp
    private LocalDateTime createdAt;
}