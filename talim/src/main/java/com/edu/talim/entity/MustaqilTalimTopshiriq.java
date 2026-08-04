package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mustaqil_talim_topshiriqlar")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MustaqilTalimTopshiriq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qaysi o'qituvchi/fan/kurs/guruhga tegishli
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oqituvchi_fan_taqsimlash_id", nullable = false)
    private OqituvchiFanTaqsimlash oqituvchiFanTaqsimlash;

    // Qaysi mavzuga tegishli (DarsJurnali, darsTuri=MUSTAQIL_TALIM)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dars_jurnali_id", nullable = false)
    private DarsJurnali darsJurnali;

    // Topshiriq turi: Referat, Esse, Test, Taqdimot, Tahliliy ma'lumot va h.k. (erkin matn)
    @Column(nullable = false, length = 100)
    private String topshiriqTuri;

    // Topshiriq nomi
    @Column(nullable = false, length = 255)
    private String nomi;

    // Izoh (topshiriq tavsifi)
    @Column(columnDefinition = "TEXT")
    private String izoh;

    private LocalDateTime boshlanishSanasi;
    private LocalDateTime yakunlanishSanasi;

    // Nechta marta qayta topshirish (urinish) mumkinligi
    private Integer urinishlarSoni;

    // Status toggle: kursantlarga yuborilganmi
    @Builder.Default
    @Column(nullable = false)
    private Boolean yuborilganMi = false;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime yaratilganVaqt = LocalDateTime.now();

    // Topshiriqqa biriktirilgan fayllar (bir nechta bo'lishi mumkin)
    @OneToMany(mappedBy = "topshiriq", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TopshiriqFayl> fayllar = new ArrayList<>();

    // Tanlangan kursantlarga yuborilgan nusxalar
    @OneToMany(mappedBy = "topshiriq", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TopshiriqYuborish> yuborishlar = new ArrayList<>();
}