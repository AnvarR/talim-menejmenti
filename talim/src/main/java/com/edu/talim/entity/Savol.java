package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "savollar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Savol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authorId;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String authorType;

    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String mavzu;

    @Column(nullable = false, columnDefinition = "text")
    private String mazmun;

    @Column(columnDefinition = "varchar(500)")
    private String faylUrl;

    @Column(nullable = false)
    @Builder.Default
    private Integer korishlarSoni = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;
}