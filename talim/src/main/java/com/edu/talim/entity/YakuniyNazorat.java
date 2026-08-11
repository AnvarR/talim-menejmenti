package com.edu.talim.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "yakuniy_nazoratlar")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class YakuniyNazorat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oqituvchi_fan_taqsimlash_id", nullable = false)
    private OqituvchiFanTaqsimlash oqituvchiFanTaqsimlash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oquv_yili_id", nullable = false)
    private OquvYili oquvYili;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Yakuniy nazorat (imtihon) bahosi
    @Column(name = "yn_baho")
    private Integer ynBaho;

    // Imtihon topshirilgan sana (semestr oxirida o'qituvchi kiritadi)
    @Column(name = "yakunlanish_sanasi")
    private LocalDate yakunlanishSanasi;

    // Agar birinchi baho 2 bo'lsa, qayta topshirgandan keyingi baho shu yerga yoziladi
    @Column(name = "qayta_topshirish_baho")
    private Integer qaytaTopshirishBaho;
}