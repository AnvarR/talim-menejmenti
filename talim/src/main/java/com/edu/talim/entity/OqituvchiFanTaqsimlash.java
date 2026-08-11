package com.edu.talim.entity;

import com.edu.talim.entity.enums.DarsTuri;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "oqituvchi_fan_taqsimlash")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OqituvchiFanTaqsimlash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O'quv bo'limi taqsimlagan fan (FanTaqsimlash dan)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fan_taqsimlash_id", nullable = false)
    private FanTaqsimlash fanTaqsimlash;

    // O'qituvchi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oqituvchi_id", nullable = false)
    private User oqituvchi;

    // Dars turi: MARUZA, SEMINAR, MUSTAQIL_TALIM
    @Column(nullable = false, columnDefinition = "varchar(50)")
    @Enumerated(EnumType.STRING)
    private DarsTuri darsTuri;

    // Soat hajmi (dars turidan avtomatik olinadi)
    @Column(nullable = false)
    private Integer soatHajmi;

    // Kurs
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kurs_id", nullable = false)
    private Course kurs;

    // Guruhlar (bir nechta)
    @ManyToMany
    @JoinTable(
            name = "oqituvchi_fan_taqsimlash_guruhlar",
            joinColumns = @JoinColumn(name = "oqituvchi_fan_taqsimlash_id"),
            inverseJoinColumns = @JoinColumn(name = "guruh_id")
    )
    private List<Group> guruhlar;

    // Fakultet boshlig'i tomonidan beriladigan ruxsatlar - shu ruxsat berilmaguncha
    // o'qituvchi tegishli nazorat sanasini kiritolmaydi
    @Column(nullable = false)
    @Builder.Default
    private Boolean oraliqNazoratRuxsat = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean yakuniyNazoratRuxsat = false;
}