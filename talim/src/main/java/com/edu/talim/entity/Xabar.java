package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Shaxsiy xabarlar entity si.
 * Har qanday foydalanuvchi (hodim yoki kursant) boshqa foydalanuvchiga xabar yuborishi mumkin.
 * senderType / receiverType: "USER" = hodim, "STUDENT" = kursant/tinglovchi
 */
@Entity
@Table(name = "xabarlar")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Xabar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Yuboruvchi
    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String senderType; // "USER" yoki "STUDENT"

    // Qabul qiluvchi
    @Column(nullable = false)
    private String receiverId;

    @Column(nullable = false, columnDefinition = "varchar(20)")
    private String receiverType; // "USER" yoki "STUDENT"

    // Xabar ma'lumotlari
    @Column(nullable = false, columnDefinition = "varchar(255)")
    private String mavzu;

    @Column(nullable = false, columnDefinition = "text")
    private String mazmun;

    // O'qilganmi
    @Column(nullable = false)
    @Builder.Default
    private Boolean oqilgan = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}