package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "amaliyot_baholari")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AmaliyotBaho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amaliyot_id", nullable = false)
    private Amaliyot amaliyot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Baho hali qo'yilmagan bo'lsa - null
    private Integer baho;

    // Agar birinchi baho 2 bo'lsa, qayta topshirgandan keyingi baho shu yerga yoziladi
    private Integer qaytaTopshirishBaho;
}