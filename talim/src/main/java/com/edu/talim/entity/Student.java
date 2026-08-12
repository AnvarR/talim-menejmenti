package com.edu.talim.entity;

import com.edu.talim.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== UMUMIY MA'LUMOT =====

    @Column(columnDefinition = "varchar(255)")
    private String photoUrl;

    @Column(unique = true, nullable = false, columnDefinition = "varchar(14)")
    private String jshshir;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String fio;

    @Enumerated(EnumType.STRING)
    private Malumot malumoti;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String passportSeria;

    @Column(nullable = false)
    private LocalDate hujjatBerilganSana;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Jins jinsi;

    @Column(nullable = false)
    private LocalDate tugilganSana;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Millat millati;

    @Column(columnDefinition = "varchar(255)")
    private String hujjatBerganTashkilot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Fuqarolik fuqaroligi;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String telefon1;

    @Column(columnDefinition = "varchar(20)")
    private String telefon2;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String email1;

    @Column(columnDefinition = "varchar(255)")
    private String email2;

    // ===== ISH JOYI VA LAVOZIMI =====

    @Column(columnDefinition = "varchar(255)")
    private String harbiyUnvoni;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String guvohnomaNomeri;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(columnDefinition = "varchar(255)")
    private String lavozimi;

    // ===== TIZIM =====

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(100)")
    private Role role;

    @Column(columnDefinition = "varchar(255)")
    private String username;

    @Column(columnDefinition = "varchar(255)")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentType type;

    // Kursantning tizimdagi umumiy holati: FAOL / CHETLATILGAN / BITIRGAN.
    // Standart kursantlar ro'yxatida (StudentRepository.findAllWithFilters) faqat
    // FAOL kursantlar ko'rsatiladi - chetlatilgan/bitirganlar avtomatik chiqarib tashlanadi.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    @Builder.Default
    private TalabaHolati holati = TalabaHolati.FAOL;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}