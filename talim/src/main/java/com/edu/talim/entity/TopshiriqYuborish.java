package com.edu.talim.entity;

import com.edu.talim.entity.enums.TopshiriqHolati;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topshiriq_yuborishlar")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TopshiriqYuborish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topshiriq_id", nullable = false)
    private MustaqilTalimTopshiriq topshiriq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Har bir kursant uchun alohida belgilanadigan muddat
    @Column(nullable = false)
    private LocalDateTime muddat;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private TopshiriqHolati holati = TopshiriqHolati.BERILDI;

    // Kursantning javob(lar)i (bir nechta urinish bo'lishi mumkin)
    @OneToMany(mappedBy = "topshiriqYuborish", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TopshiriqJavob> javoblar = new ArrayList<>();
}