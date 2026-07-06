package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "oquv_yillari")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OquvYili {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom; // "2025-2026"

    @Column(nullable = false)
    private Integer boshlanishYil; // 2025

    @Column(nullable = false)
    private Integer tugashYil; // 2026

    @Column(nullable = false)
    private Boolean faol = false;
}