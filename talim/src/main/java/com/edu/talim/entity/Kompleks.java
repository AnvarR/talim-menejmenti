package com.edu.talim.entity;

import com.edu.talim.entity.enums.MaterialKategoriyasi;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "komplekslar")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Kompleks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qaysi fan/o'qituvchi taqsimotiga tegishli (kurs, guruh, fan shu orqali aniqlanadi)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oqituvchi_fan_taqsimlash_id", nullable = false)
    private OqituvchiFanTaqsimlash oqituvchiFanTaqsimlash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oquv_yili_id", nullable = false)
    private OquvYili oquvYili;

    @Column(nullable = false)
    private String materialNomi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(50)")
    private MaterialKategoriyasi materialKategoriyasi;

    @Column(nullable = false)
    private LocalDateTime biriktirilganVaqt;

    @OneToMany(mappedBy = "kompleks", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KompleksFayl> fayllar;
}