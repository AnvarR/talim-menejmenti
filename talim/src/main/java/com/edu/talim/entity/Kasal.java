package com.edu.talim.entity;

import com.edu.talim.entity.enums.KasalYuborilganJoy;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "kasallar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kasal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, columnDefinition = "text")
    private String kasallikSababi;

    @Column(nullable = false)
    private LocalDate kiritilganSana;

    @Column(nullable = false)
    private LocalTime murojaatvaqti;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KasalYuborilganJoy kasalYuborilganJoy;

    @Column(columnDefinition = "varchar(255)")
    private String mutaxassisTuri;

    @Column(nullable = false)
    private LocalDate boshlanishSanasi;

    @Column(nullable = false)
    private LocalDate tugashSanasi;

    // Avtomatik hisoblanadi
    private Long muddat;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        // Muddat avtomatik hisoblanadi
        if (boshlanishSanasi != null && tugashSanasi != null) {
            this.muddat = ChronoUnit.DAYS.between(boshlanishSanasi, tugashSanasi);
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (boshlanishSanasi != null && tugashSanasi != null) {
            this.muddat = ChronoUnit.DAYS.between(boshlanishSanasi, tugashSanasi);
        }
    }
}