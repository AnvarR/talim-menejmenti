package com.edu.talim.service;

import java.util.UUID;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.MustaqilTalimJurnalResponseDTO;
import com.edu.talim.entity.*;
import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.entity.enums.Semestr;
import com.edu.talim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MustaqilTalimJurnalService {

    // SQL "date" turi uchun xavfsiz, uzoq kelajakdagi sana (LocalDate.MAX ishlatilmaydi -
    // u SQL date chegarasidan oshib, "date out of range" xatosiga olib keladi)
    private static final LocalDate CHEKSIZ_KELAJAK = LocalDate.of(2100, 12, 31);

    private final MustaqilTalimTopshiriqRepository topshiriqRepository;
    private final TopshiriqYuborishRepository topshiriqYuborishRepository;
    private final TopshiriqJavobRepository topshiriqJavobRepository;
    private final OqituvchiFanTaqsimlashRepository oqituvchiFanTaqsimlashRepository;
    private final OraliqNazoratRepository oraliqNazoratRepository;
    private final StudentRepository studentRepository;

    public MustaqilTalimJurnalResponseDTO getJurnal(UUID oqituvchiFanTaqsimlashId,
                                                    Semestr semestr, UUID oquvYiliId) {

        OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi"));

        // Shu semestr/yilga tegishli barcha topshiriqlar (har biri — bitta sana ustuni)
        List<MustaqilTalimTopshiriq> topshiriqlar = topshiriqRepository
                .findByOqituvchiFanTaqsimlashId(oqituvchiFanTaqsimlashId)
                .stream()
                .filter(t -> t.getDarsJurnali().getOquvYili().getId().equals(oquvYiliId))
                .filter(t -> t.getDarsJurnali().getSemestr() == semestr)
                .sorted(Comparator.comparing(MustaqilTalimTopshiriq::getYakunlanishSanasi,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        // Kesim sanalari uchun "opa-uka" (sibling) taqsimlashni topish —
        // Amaliy/Seminar modulida kiritilgan oraliq kesim sanalari qayta ishlatiladi.
        // MUHIM: faqat GURUHI ham bir xil bo'lgan taqsimlash to'g'ri "birodar" hisoblanadi,
        // aks holda boshqa guruhning kesim sanasi noto'g'ri ishlatilib qolishi mumkin.
        Set<UUID> mendagiGuruhIdlar = taqsimlash.getGuruhlar() == null ? Set.of() :
                taqsimlash.getGuruhlar().stream().map(Group::getId).collect(Collectors.toSet());

        List<OqituvchiFanTaqsimlash> birodarlar = oqituvchiFanTaqsimlashRepository
                .findByFanTaqsimlashIdAndOqituvchiIdAndKursId(
                        taqsimlash.getFanTaqsimlash().getId(),
                        taqsimlash.getOqituvchi().getId(),
                        taqsimlash.getKurs().getId())
                .stream()
                .filter(t -> t.getDarsTuri() != DarsTuri.MUSTAQIL_TALIM)
                .filter(t -> t.getGuruhlar() != null && t.getGuruhlar().stream()
                        .anyMatch(g -> mendagiGuruhIdlar.contains(g.getId())))
                .collect(Collectors.toList());

        List<OraliqNazorat> oraliq1lar = List.of();
        List<OraliqNazorat> oraliq2lar = List.of();
        List<OraliqNazorat> yilOraliqlari = List.of();

        if (!birodarlar.isEmpty()) {
            UUID birodarTaqsimlashId = birodarlar.get(0).getId();
            oraliq1lar = oraliqNazoratRepository
                    .findByOqituvchiFanTaqsimlashIdAndOquvYiliIdAndSemestrAndOraliqRaqami(
                            birodarTaqsimlashId, oquvYiliId, semestr, 1);
            oraliq2lar = oraliqNazoratRepository
                    .findByOqituvchiFanTaqsimlashIdAndOquvYiliIdAndSemestrAndOraliqRaqami(
                            birodarTaqsimlashId, oquvYiliId, semestr, 2);
            yilOraliqlari = oraliqNazoratRepository
                    .findByOqituvchiFanTaqsimlashIdAndOquvYiliId(birodarTaqsimlashId, oquvYiliId);
        }

        // Guruhidagi kursantlar
        List<Student> kursantlar = new ArrayList<>();
        if (taqsimlash.getGuruhlar() != null) {
            for (var guruh : taqsimlash.getGuruhlar()) {
                kursantlar.addAll(studentRepository.findByGroupIdOrderByFioAsc(guruh.getId()));
            }
        }

        LocalDate oquvYiliBoshlanish = topshiriqlar.isEmpty() ? null :
                LocalDate.of(topshiriqlar.get(0).getDarsJurnali().getOquvYili().getBoshlanishYil(), 9, 1);

        List<OraliqNazorat> oraliq1larFinal = oraliq1lar;
        List<OraliqNazorat> oraliq2larFinal = oraliq2lar;
        List<OraliqNazorat> yilOraliqlariFinal = yilOraliqlari;
        LocalDate oquvYiliBoshlanishFinal = oquvYiliBoshlanish;

        List<MustaqilTalimJurnalResponseDTO.KursantMTJurnalDTO> kursantDTOlar = kursantlar.stream()
                .map(student -> buildKursant(student, topshiriqlar, oraliq1larFinal, oraliq2larFinal,
                        yilOraliqlariFinal, oquvYiliBoshlanishFinal))
                .collect(Collectors.toList());

        List<MustaqilTalimJurnalResponseDTO.TopshiriqUstunDTO> ustunlar = topshiriqlar.stream()
                .map(t -> MustaqilTalimJurnalResponseDTO.TopshiriqUstunDTO.builder()
                        .topshiriqId(t.getId())
                        .muddat(t.getYakunlanishSanasi())
                        .soat(2)
                        .nomi(t.getNomi())
                        .build())
                .collect(Collectors.toList());

        String guruhNomi = taqsimlash.getGuruhlar() != null ?
                taqsimlash.getGuruhlar().stream()
                        .map(Group::getGuruhNomi)
                        .collect(Collectors.joining(", ")) : null;

        return MustaqilTalimJurnalResponseDTO.builder()
                .oqituvchiFanTaqsimlashId(oqituvchiFanTaqsimlashId)
                .fanNomi(taqsimlash.getFanTaqsimlash().getFan().getFanNomi())
                .oqituvchiFio(taqsimlash.getOqituvchi().getFio())
                .kursNomi(taqsimlash.getKurs().getKursRaqami() + "-kurs")
                .guruhNomi(guruhNomi)
                .oquvYiliNomi(topshiriqlar.isEmpty() ? null :
                        topshiriqlar.get(0).getDarsJurnali().getOquvYili().getNom())
                .semestr(semestr)
                .topshiriqlar(ustunlar)
                .kursantlar(kursantDTOlar)
                .build();
    }

    // Boshqa modul (masalan Seminar/Amaliy jurnal) uchun R(MT-1)/R(MT-2)ni hisoblab beradi.
    // kesim1Sanasi/kesim2Sanasi - chaqiruvchi modulning O'ZINING kesim sanalari beriladi
    // (masalan Seminar jurnalining R(KB) uchun ishlatgan kesim sanalari qayta ishlatiladi).
    // mtTaqsimlashCandidateIds - bir xil fanTaqsimlash+guruhga tegishli BARCHA "birodar" taqsimlashlar ID'lari
    // (faqat MUSTAQIL_TALIM turi emas) - chunki frontend topshiriqni ba'zan noto'g'ri darsTuridagi
    // taqsimlashga yozib qo'yishi mumkin, shuning uchun barcha nomzodlar orasidan qidiramiz.
    public Double[] hisoblaRmt1Rmt2(List<UUID> mtTaqsimlashCandidateIds, UUID studentId, Semestr semestr, UUID oquvYiliId,
                                    LocalDate kesim1Sanasi, LocalDate kesim2Sanasi) {
        if (mtTaqsimlashCandidateIds == null || mtTaqsimlashCandidateIds.isEmpty()) return new Double[]{null, null};

        List<MustaqilTalimTopshiriq> topshiriqlar = topshiriqRepository
                .findByOqituvchiFanTaqsimlashIdIn(mtTaqsimlashCandidateIds)
                .stream()
                .filter(t -> t.getDarsJurnali().getOquvYili().getId().equals(oquvYiliId))
                .filter(t -> t.getDarsJurnali().getSemestr() == semestr)
                .collect(Collectors.toList());

        if (topshiriqlar.isEmpty()) return new Double[]{null, null};

        Map<Long, Integer> topshiriqBaho = topshiriqBahoMap(topshiriqlar, studentId);

        LocalDate oquvYiliBoshlanish = LocalDate.of(
                topshiriqlar.get(0).getDarsJurnali().getOquvYili().getBoshlanishYil(), 9, 1);

        LocalDate effektivKesim1 = kesim1Sanasi != null ? kesim1Sanasi : CHEKSIZ_KELAJAK;
        Double rmt1 = hisoblaRmt(topshiriqlar, topshiriqBaho, oquvYiliBoshlanish, effektivKesim1);

        Double rmt2 = null;
        if (kesim1Sanasi != null) {
            LocalDate effektivKesim2 = kesim2Sanasi != null ? kesim2Sanasi : CHEKSIZ_KELAJAK;
            rmt2 = hisoblaRmt(topshiriqlar, topshiriqBaho, kesim1Sanasi, effektivKesim2);
        }

        return new Double[]{rmt1, rmt2};
    }

    // Kursantning shu fan/mavzular bo'yicha topshiriq -> baho map'i
    private Map<Long, Integer> topshiriqBahoMap(List<MustaqilTalimTopshiriq> topshiriqlar, UUID studentId) {
        Map<Long, Integer> topshiriqBaho = new HashMap<>();
        for (MustaqilTalimTopshiriq t : topshiriqlar) {
            Integer baho = topshiriqYuborishRepository
                    .findByTopshiriqIdAndStudentId(t.getId(), studentId)
                    .stream()
                    .findFirst()
                    .map(y -> topshiriqJavobRepository
                            .findByTopshiriqYuborishIdOrderByBerilganSanaDesc(y.getId())
                            .stream()
                            .map(TopshiriqJavob::getBaho)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null))
                    .orElse(null);
            if (baho != null) {
                topshiriqBaho.put(t.getId(), baho);
            }
        }
        return topshiriqBaho;
    }

    private MustaqilTalimJurnalResponseDTO.KursantMTJurnalDTO buildKursant(
            Student student,
            List<MustaqilTalimTopshiriq> topshiriqlar,
            List<OraliqNazorat> oraliq1lar,
            List<OraliqNazorat> oraliq2lar,
            List<OraliqNazorat> yilOraliqlari,
            LocalDate oquvYiliBoshlanish) {

        // Har bir topshiriq uchun kursantning bahosi (topshiriq -> baho map)
        Map<Long, Integer> topshiriqBaho = new HashMap<>();
        for (MustaqilTalimTopshiriq t : topshiriqlar) {
            Integer baho = topshiriqYuborishRepository
                    .findByTopshiriqIdAndStudentId(t.getId(), student.getId())
                    .stream()
                    .findFirst()
                    .map(y -> topshiriqJavobRepository
                            .findByTopshiriqYuborishIdOrderByBerilganSanaDesc(y.getId())
                            .stream()
                            .map(TopshiriqJavob::getBaho)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null))
                    .orElse(null);
            if (baho != null) {
                topshiriqBaho.put(t.getId(), baho);
            }
        }

        List<Integer> baholarRoyxati = topshiriqlar.stream()
                .map(t -> topshiriqBaho.get(t.getId()))
                .collect(Collectors.toList());

        List<OraliqNazorat> mendagiYilOraliqlari = yilOraliqlari.stream()
                .filter(o -> o.getStudent().getId().equals(student.getId()))
                .collect(Collectors.toList());

        OraliqNazorat oraliq1 = oraliq1lar.stream()
                .filter(o -> o.getStudent().getId().equals(student.getId()))
                .findFirst().orElse(null);
        OraliqNazorat oraliq2 = oraliq2lar.stream()
                .filter(o -> o.getStudent().getId().equals(student.getId()))
                .findFirst().orElse(null);

        Double rmt1;
        LocalDateTime kesim1 = null;
        LocalDate kesim1DateEffektiv = CHEKSIZ_KELAJAK;
        if (oraliq1 != null && oraliq1.getKesimSanasi() != null) {
            kesim1DateEffektiv = oraliq1.getKesimSanasi();
            kesim1 = kesim1DateEffektiv.atTime(23, 59);
        }
        LocalDate boshlanish1 = oldingiKesimSanasi(mendagiYilOraliqlari, kesim1DateEffektiv, oquvYiliBoshlanish);
        rmt1 = hisoblaRmt(topshiriqlar, topshiriqBaho, boshlanish1, kesim1DateEffektiv);

        Double rmt2;
        LocalDateTime kesim2 = null;

        if (kesim1 == null) {
            // 1-oraliq hali "qulflanmagan" - 2-oraliq davri hali boshlanmagan
            rmt2 = null;
        } else {
            LocalDate kesim2DateEffektiv = CHEKSIZ_KELAJAK;
            if (oraliq2 != null && oraliq2.getKesimSanasi() != null) {
                kesim2DateEffektiv = oraliq2.getKesimSanasi();
                kesim2 = kesim2DateEffektiv.atTime(23, 59);
            }
            // 2-oraliq har doim 1-oraliq tugagan joydan (kesim1DateEffektiv) boshlanadi
            rmt2 = hisoblaRmt(topshiriqlar, topshiriqBaho, kesim1DateEffektiv, kesim2DateEffektiv);
        }

        return MustaqilTalimJurnalResponseDTO.KursantMTJurnalDTO.builder()
                .studentId(student.getId())
                .studentFio(student.getFio())
                .baholar(baholarRoyxati)
                .rmt1(rmt1)
                .kesim1Sanasi(kesim1)
                .rmt2(rmt2)
                .kesim2Sanasi(kesim2)
                .build();
    }

    // R(MT) — berilgan sana oralig'idagi topshiriq baholarining yaxlitlangan o'rtachasi
    private Double hisoblaRmt(List<MustaqilTalimTopshiriq> topshiriqlar, Map<Long, Integer> topshiriqBaho,
                              LocalDate boshlanish, LocalDate tugash) {
        if (boshlanish == null || tugash == null) return null;

        List<Integer> baholar = topshiriqlar.stream()
                .filter(t -> t.getYakunlanishSanasi() != null)
                .filter(t -> {
                    LocalDate sana = t.getYakunlanishSanasi().toLocalDate();
                    return sana.isAfter(boshlanish) && !sana.isAfter(tugash);
                })
                .map(t -> topshiriqBaho.get(t.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (baholar.isEmpty()) return null;

        double sum = baholar.stream().mapToInt(Integer::intValue).sum();
        return yaxlaBaho(sum / baholar.size());
    }

    // Kursant bo'yicha shu oraliqdan oldingi eng yaqin kesim sanasini topadi
    private LocalDate oldingiKesimSanasi(List<OraliqNazorat> studentBoyichaOraliqlar,
                                         LocalDate joriyKesim, LocalDate oquvYiliBoshlanish) {
        return studentBoyichaOraliqlar.stream()
                .map(OraliqNazorat::getKesimSanasi)
                .filter(sana -> sana != null && sana.isBefore(joriyKesim))
                .max(LocalDate::compareTo)
                .orElse(oquvYiliBoshlanish);
    }

    // Maxsus yaxlitlash qoidasi (0.51 chegarasi) — ElektronJurnalService bilan bir xil mantiq
    private Double yaxlaBaho(Double qiymat) {
        if (qiymat == null) return null;
        long butun = (long) Math.floor(qiymat);
        double kasr = qiymat - butun;
        return kasr >= 0.51 ? (double) (butun + 1) : (double) butun;
    }
}