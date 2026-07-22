package com.edu.talim.entity;

import com.edu.talim.entity.enums.Semestr;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "oraliq_nazoratlar")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OraliqNazorat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qaysi o'qituvchi fan taqsimlashga tegishli
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oqituvchi_fan_taqsimlash_id", nullable = false)
    private OqituvchiFanTaqsimlash oqituvchiFanTaqsimlash;

    // Qaysi kursant
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // O'quv yili
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oquv_yili_id", nullable = false)
    private OquvYili oquvYili;

    // 1-semestr yoki 2-semestr
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20)")
    private Semestr semestr;

    // Oraliq nazorat bahosi (qo'lda kiritiladi): 3, 4, 5
    @Column
    private Integer ronBaho;
}