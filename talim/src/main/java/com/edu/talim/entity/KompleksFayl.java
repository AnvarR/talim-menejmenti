package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kompleks_fayllar")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KompleksFayl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kompleks_id", nullable = false)
    private Kompleks kompleks;

    @Column(nullable = false)
    private String faylNomi; // original nom

    @Column(nullable = false)
    private String faylYoli; // komplekslar/...

    @Column(nullable = false)
    private String faylTuri; // pdf, mp4, docx...
}