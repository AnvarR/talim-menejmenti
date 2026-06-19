package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Savol-javoblar moduli uchun Javob entity si.
 * Har bir javob bitta savolga tegishli.
 */
@Entity
@Table(name = "javoblar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Javob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qaysi savolga javob
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savol_id", nullable = false)
    private Savol savol;

    // Javob beruvchi
    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String authorType; // "USER" yoki "STUDENT"

    @Column(nullable = false, columnDefinition = "text")
    private String mazmun;

    @CreationTimestamp
    private LocalDateTime createdAt;
}