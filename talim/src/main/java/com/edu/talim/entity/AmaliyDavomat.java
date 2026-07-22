package com.edu.talim.entity;

import com.edu.talim.entity.enums.DavomatHolati;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "amaliy_davomatlar")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AmaliyDavomat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qaysi darsga tegishli
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dars_jurnali_id", nullable = false)
    private DarsJurnali darsJurnali;

    // Qaysi kursant
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Davomat holati: N, K, S, Y (qayta topshirish baho orqali)
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10)")
    private DavomatHolati holat;

    // Kunlik baho: 3, 4, 5 (null = baholanmagan)
    @Column
    private Integer baho;

    // Qayta topshirishda olgan baho: 3, 4, 5
    @Column
    private Integer qaytaTopshirishBaho;

    // Blok qoidasi: 7 kun ichida qayta topshirmasa true
    @Builder.Default
    @Column(nullable = false)
    private Boolean bloklanganMi = false;

    // Blok sanasi
    @Column
    private LocalDate bloklashSanasi;
}