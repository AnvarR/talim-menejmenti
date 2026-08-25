package com.edu.talim.entity;

import com.edu.talim.entity.enums.XizmatOtashJoyi;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sutkalik_naryadlar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SutkalikNaryad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(100)")
    private XizmatOtashJoyi xizmatOtashJoyi;

    @Column(nullable = false)
    private LocalDate qabulQilishSanasi;

    @Column(nullable = false)
    private LocalDate topshirishSanasi;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String oquvYili; // "2024-2025"

    @CreationTimestamp
    private LocalDateTime createdAt;
}