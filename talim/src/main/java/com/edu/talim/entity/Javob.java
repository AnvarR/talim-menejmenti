package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Qaysi savolga javob
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savol_id", nullable = false, foreignKey = @ForeignKey(name = "fk_javob_savol"))
    private Savol savol;

    // Javob beruvchi
    @Column(nullable = false)
    private String authorId;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String authorType; // "USER" yoki "STUDENT"

    @Column(nullable = false, columnDefinition = "text")
    private String mazmun;

    @CreationTimestamp
    private LocalDateTime createdAt;
}