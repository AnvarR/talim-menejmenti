package com.edu.talim.entity;

import com.edu.talim.entity.enums.ChiqishSababi;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "institutdan_chiqishlar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstitutdanChiqish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(100)")
    private ChiqishSababi chiqishSababi;

    @Column(columnDefinition = "text")
    private String izoh;

    // Chiqish
    @Column(nullable = false)
    private LocalDate chiqganSana;

    private LocalTime chiqganVaqt;

    // Qaytish
    private LocalDate qaytganSana;

    private LocalTime qaytganVaqt;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String oquvYili;

    @CreationTimestamp
    private LocalDateTime createdAt;
}