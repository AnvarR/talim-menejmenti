package com.edu.talim.entity;

import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.entity.enums.Semestr;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dars_jurnali")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DarsJurnali {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qaysi o'qituvchi, fan, kurs, guruhga tegishli
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oqituvchi_fan_taqsimlash_id", nullable = false)
    private OqituvchiFanTaqsimlash oqituvchiFanTaqsimlash;

    // Faol o'quv yili avtomatik olinadi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oquv_yili_id", nullable = false)
    private OquvYili oquvYili;

    // Dars turi: MARUZA, SEMINAR, MUSTAQIL_TALIM, AMALIYOT_KURS_ISHI
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(30)")
    private DarsTuri darsTuri;

    // 1-semestr yoki 2-semestr (o'qituvchi dars yaratishda tanlaydi)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private Semestr semestr;

    // Dars o'tilgan sana
    @Column(nullable = false)
    private LocalDate sana;

    // Dars soati (default 2)
    @Builder.Default
    @Column(nullable = false)
    private Integer soat = 2;

    // Mashg'ulot mavzusi nomi
    @Column
    private String mavzuNomi;

    // Topshiriq fayli
    @Column
    private String topshiriqFaylYoli;

    @Column
    private String topshiriqFaylNomi;

    // Shu darsdagi barcha davomatlar (EAGER — response da ko'rinsin)
    @OneToMany(mappedBy = "darsJurnali", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.EAGER)
    @Builder.Default
    private List<Davomat> davomatlar = new ArrayList<>();
}