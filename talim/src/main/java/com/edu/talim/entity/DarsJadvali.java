package com.edu.talim.entity;

import com.edu.talim.entity.enums.HaftaKuni;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dars_jadvali")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DarsJadvali {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kurs_id", nullable = false)
    private Course kurs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oquv_yili_id", nullable = false)
    private OquvYili oquvYili;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HaftaKuni haftaKuni;

    @Column(nullable = false)
    private String faylNomi; // original fayl nomi

    @Column(nullable = false)
    private String faylYoli; // uploads/dars-jadvali/...

    @Column(nullable = false)
    private String faylTuri; // pdf, xlsx, doc
}