package com.edu.talim.entity;

import com.edu.talim.entity.enums.KochirishTuri;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// Kursant bilan sodir bo'lgan har bir kursdan-kursga o'tish/chetlatish/arxivlash
// hodisasi shu yerda tarixiy yozuv sifatida saqlanadi. Hech qanday eski ma'lumot
// o'chirilmaydi - Student.course/holati faqat "joriy holat"ni ko'rsatadi,
// bu jadval esa "qachon, qaysi kursdan qaysi kursga" bo'lganini tarixiy saqlaydi.
@Entity
@Table(name = "kurs_kochirish_tarixi")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KursKochirishTarixi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Hodisa turi: KOCHIRISH / CHETLATISH / ARXIVLASH
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private KochirishTuri turi;

    // Ko'chirilgunga qadar qaysi kursda edi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eski_kurs_id", nullable = false)
    private Course eskiKurs;

    // Qaysi kursga o'tkazildi (CHETLATISH/ARXIVLASH bo'lsa - null, keyingi kurs yo'q)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "yangi_kurs_id")
    private Course yangiKurs;

    // Shu hodisa qaysi o'quv yilida sodir bo'ldi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oquv_yili_id", nullable = false)
    private OquvYili oquvYili;

    // Faqat CHETLATISH uchun - sababi (fakultet boshlig'i ko'rsatkichlar asosida yozadi)
    @Column(columnDefinition = "text")
    private String sababi;

    @Column(nullable = false)
    private LocalDate sana;

    @CreationTimestamp
    private LocalDateTime yaratilganVaqt;
}