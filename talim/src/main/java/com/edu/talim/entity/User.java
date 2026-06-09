package com.edu.talim.entity;

import com.edu.talim.entity.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== UMUMIY MA'LUMOT =====

    @Column(columnDefinition = "varchar(255)")
    private String photoUrl;

    @Column(unique = true, nullable = false, columnDefinition = "varchar(14)")
    private String jshshir;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String passportMalumotlari;

    @Column(nullable = false)
    private LocalDate tugilganSana;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Fuqarolik fuqaroligi;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String fio;

    @Column(nullable = false)
    private LocalDate hujjatBerilganSana;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Millat millati;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String telefon1;

    @Column(columnDefinition = "varchar(20)")
    private String telefon2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Malumot malumoti;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Jins jinsi;

    @Column(columnDefinition = "varchar(255)")
    private String hujjatBerganTashkilot;

    @Column(columnDefinition = "varchar(255)")
    private String pochtaManzili;

    // ===== ISH JOYI VA LAVOZIMI =====

    @ManyToOne
    @JoinColumn(name = "tarkibiy_tuzilma_id")
    private TarkibiyTuzilma tarkibiyTuzilma;

    @Column(columnDefinition = "varchar(255)")
    private String lavozimi;

    @Column(columnDefinition = "varchar(255)")
    private String ilmiyUnvoni;

    @Column(columnDefinition = "varchar(255)")
    private String ilmiyDarajasi;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String guvohnomaNomeri;

    @Column(columnDefinition = "varchar(255)")
    private String harbiyUnvoni;

    // ===== TIZIM =====

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}