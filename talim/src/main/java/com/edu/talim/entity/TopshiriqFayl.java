package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "topshiriq_fayllar")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TopshiriqFayl {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topshiriq_id", nullable = false)
    private MustaqilTalimTopshiriq topshiriq;

    @Column(nullable = false)
    private String faylNomi;

    @Column(nullable = false)
    private String faylYoli;
}