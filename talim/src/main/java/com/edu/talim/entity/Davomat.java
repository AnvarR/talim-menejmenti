package com.edu.talim.entity;

import com.edu.talim.entity.enums.DavomatHolati;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "davomatlar")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Davomat {

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

    // Davomat holati: N, K, S, Y, N_T, K_T, S_T, Y_T
    // null = darsda qatnashgan (bo'sh katak)
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10)")
    private DavomatHolati holat;

    // Blok qoidasi: 7 kun ichida qayta topshirmasa true bo'ladi
    @Column(nullable = false)
    private Boolean bloklanganMi = false;

    // Blok sanasi (qachon bloklanganini bilish uchun)
    @Column
    private LocalDate bloklashSanasi;
}