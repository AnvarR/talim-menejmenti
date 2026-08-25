package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "kurs_ishi_baholari")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KursIshiBaho {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kurs_ishi_id", nullable = false)
    private KursIshi kursIshi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Baho hali qo'yilmagan bo'lsa - null
    private Integer baho;

    // Agar birinchi baho 2 bo'lsa, qayta topshirgandan keyingi baho shu yerga yoziladi
    private Integer qaytaTopshirishBaho;
}