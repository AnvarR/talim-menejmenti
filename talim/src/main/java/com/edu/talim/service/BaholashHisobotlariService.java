package com.edu.talim.service;

import java.util.UUID;

import com.edu.talim.dto.*;
import com.edu.talim.entity.*;
import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.entity.enums.Semestr;
import com.edu.talim.exception.NotFoundException;
import com.edu.talim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BaholashHisobotlariService {

    private final ElektronJurnalService elektronJurnalService;
    private final OqituvchiFanTaqsimlashRepository taqsimlashRepository;
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final XabarService xabarService;

    // ============ 1) Individual baholar - bitta kursantning shu semestrdagi barcha fanlari ============
    public List<HisobotSatriDTO> individualBaholar(UUID studentId, Long oquvYiliId, Semestr semestr) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Kursant topilmadi: " + studentId));
        if (student.getGroup() == null) {
            return List.of();
        }

        List<OqituvchiFanTaqsimlash> fanlar = taqsimlashRepository
                .findByDarsTuriAndGuruhlarId(DarsTuri.SEMINAR, student.getGroup().getId());

        String kursNomi = student.getCourse() != null ? student.getCourse().getKursRaqami() + "-kurs" : null;
        String guruhNomi = student.getGroup().getGuruhNomi();

        List<HisobotSatriDTO> natijalar = new ArrayList<>();
        for (OqituvchiFanTaqsimlash t : fanlar) {
            ElektronJurnalService.StudentFanNatijasi natija = elektronJurnalService.getStudentNatija(
                    t.getId(), DarsTuri.SEMINAR, semestr, oquvYiliId, studentId);
            natijalar.add(qatorYasash(studentId, student.getFio(), student.getReytingDaftarchasiRaqami(),
                    kursNomi, guruhNomi, natija));
        }
        return natijalar;
    }

    // ============ 2) Fan bo'yicha - bitta fandan butun guruhning hisoboti ============
    public List<HisobotSatriDTO> fanBoyicha(UUID fanId, UUID kursId, UUID guruhId, Long oquvYiliId, Semestr semestr) {
        var sahifa = taqsimlashRepository.findOraliqYakuniyRuxsatRoyxati(
                fanId, null, kursId, guruhId, Pageable.unpaged());
        if (sahifa.isEmpty()) {
            throw new NotFoundException("Shu fan/kurs/guruh uchun taqsimlash topilmadi");
        }
        OqituvchiFanTaqsimlash taqsimlash = sahifa.getContent().get(0);

        ElektronJurnalResponseDTO jurnal = elektronJurnalService.getJurnal(
                taqsimlash.getId(), DarsTuri.SEMINAR, semestr, oquvYiliId);

        Map<UUID, String> raqamlar = raqamlarniOl(
                jurnal.getKursantlar().stream()
                        .map(ElektronJurnalResponseDTO.KursantJurnalDTO::getStudentId)
                        .collect(Collectors.toList()));

        return jurnal.getKursantlar().stream()
                .map(kj -> qatorYasashJurnaldan(kj, raqamlar, jurnal.getKursNomi(), jurnal.getGuruhNomi(), jurnal.getFanNomi()))
                .collect(Collectors.toList());
    }

    // ============ 3) Kurs/Guruh bo'yicha - guruhlar kesimida umumiy hisobot ============
    public KursGuruhHisobotDTO kursGuruhBoyicha(Long oquvYiliId, Semestr semestr, UUID kursId, UUID guruhId) {
        if (oquvYiliId == null || semestr == null) {
            throw new RuntimeException("O'quv yili va semestr tanlanishi shart!");
        }

        List<Group> guruhlar = aniqlaGuruhlar(kursId, guruhId);

        List<GuruhHisobotSatriDTO> guruhSatrlari = new ArrayList<>();
        List<Double> hammaSemestrBaholari = new ArrayList<>();
        int jamiKursantlar = 0;

        for (Group guruh : guruhlar) {
            List<OqituvchiFanTaqsimlash> fanlar = taqsimlashRepository
                    .findByDarsTuriAndGuruhlarId(DarsTuri.SEMINAR, guruh.getId());
            if (fanlar.isEmpty()) continue;

            List<Double> joriylar = new ArrayList<>();
            List<Double> oraliqlar = new ArrayList<>();
            List<Double> yakuniylar = new ArrayList<>();
            List<Double> semestrlar = new ArrayList<>();
            Set<UUID> guruhStudentIds = new HashSet<>();

            for (OqituvchiFanTaqsimlash t : fanlar) {
                ElektronJurnalResponseDTO jurnal = elektronJurnalService.getJurnal(
                        t.getId(), DarsTuri.SEMINAR, semestr, oquvYiliId);

                for (var kj : jurnal.getKursantlar()) {
                    guruhStudentIds.add(kj.getStudentId());

                    Double joriy = ortacha(kj.getRkb1(), kj.getRkb2());
                    Double oraliq = kj.getRonSem();
                    Integer yakuniyEff = kj.getRynQaytaTopshirishBaho() != null
                            ? kj.getRynQaytaTopshirishBaho() : kj.getRyn();
                    Double yakuniy = yakuniyEff != null ? yakuniyEff.doubleValue() : null;
                    // Semestr bahosi - hech qanday qayta hisoblashsiz, to'g'ridan-to'g'ri
                    // elektron jurnaldagi haqiqiy R(SEM) qiymati
                    Double semestrBahosi = kj.getRsem();

                    if (joriy != null) joriylar.add(joriy);
                    if (oraliq != null) oraliqlar.add(oraliq);
                    if (yakuniy != null) yakuniylar.add(yakuniy);
                    if (semestrBahosi != null) {
                        semestrlar.add(semestrBahosi);
                        hammaSemestrBaholari.add(semestrBahosi);
                    }
                }
            }

            jamiKursantlar += guruhStudentIds.size();

            Double guruhSemestr = ortachaRoyxat(semestrlar);
            guruhSatrlari.add(GuruhHisobotSatriDTO.builder()
                    .kursNomi(guruh.getCourse() != null ? guruh.getCourse().getKursRaqami() + "-kurs" : null)
                    .guruhNomi(guruh.getGuruhNomi())
                    .joriyBaho(ortachaRoyxat(joriylar))
                    .oraliqBaho(ortachaRoyxat(oraliqlar))
                    .yakuniyBaho(ortachaRoyxat(yakuniylar))
                    .semestrBahosi(guruhSemestr)
                    .ozlashtirishDarajasi(darajaAniqlash(guruhSemestr))
                    .build());
        }

        String engYaxshi = guruhSatrlari.stream()
                .filter(g -> g.getSemestrBahosi() != null)
                .max(Comparator.comparing(GuruhHisobotSatriDTO::getSemestrBahosi))
                .map(GuruhHisobotSatriDTO::getGuruhNomi).orElse(null);
        String nazoratga = guruhSatrlari.stream()
                .filter(g -> g.getSemestrBahosi() != null)
                .min(Comparator.comparing(GuruhHisobotSatriDTO::getSemestrBahosi))
                .map(GuruhHisobotSatriDTO::getGuruhNomi).orElse(null);

        return KursGuruhHisobotDTO.builder()
                .jamiKursantlar(jamiKursantlar)
                .ortachaBaho(ortachaRoyxat(hammaSemestrBaholari))
                .engYaxshiGuruh(engYaxshi)
                .nazoratgaOlishKerak(nazoratga)
                .guruhlar(guruhSatrlari)
                .build();
    }

    // ============ 4) Past o'zlashtiruvchilar - o'zlashtirishi <3.5 bo'lgan barcha (kursant,fan) juftliklar ============
    public List<HisobotSatriDTO> pastOzlashtiruvchilar(Long oquvYiliId, Semestr semestr,
                                                       UUID kursId, UUID guruhId, UUID fanId) {
        List<Group> guruhlar = aniqlaGuruhlar(kursId, guruhId);
        List<HisobotSatriDTO> natija = new ArrayList<>();

        for (Group guruh : guruhlar) {
            List<OqituvchiFanTaqsimlash> fanlar = taqsimlashRepository
                    .findByDarsTuriAndGuruhlarId(DarsTuri.SEMINAR, guruh.getId());
            if (fanlar.isEmpty()) continue;

            // Shu guruh kursantlarining reyting daftarchasi raqamlari - guruh uchun BITTA marta olinadi
            Map<UUID, String> raqamlar = raqamlarniOl(
                    studentRepository.findByGroupIdOrderByFioAsc(guruh.getId())
                            .stream().map(Student::getId).collect(Collectors.toList()));

            for (OqituvchiFanTaqsimlash t : fanlar) {
                if (fanId != null && !t.getFanTaqsimlash().getFan().getId().equals(fanId)) {
                    continue;
                }

                ElektronJurnalResponseDTO jurnal = elektronJurnalService.getJurnal(
                        t.getId(), DarsTuri.SEMINAR, semestr, oquvYiliId);

                for (var kj : jurnal.getKursantlar()) {
                    HisobotSatriDTO satr = qatorYasashJurnaldan(
                            kj, raqamlar, jurnal.getKursNomi(), jurnal.getGuruhNomi(), jurnal.getFanNomi());
                    // Faqat "Qoniqarli" va undan past (semestr bahosi < 3.5) - "Past o'zlashtiruvchilar"ga tushadi
                    if (satr.getSemestrBahosi() != null && satr.getSemestrBahosi() < 3.5) {
                        natija.add(satr);
                    }
                }
            }
        }
        return natija;
    }

    // ============ Ogohlantirish yuborish ============
    // Shu filtrlar bo'yicha past o'zlashtirayotgan barcha kursantlarga (bitta kursantga
    // bir nechta past fan bo'lsa - hammasi BITTA xabarga jamlanib) ogohlantirish yuboriladi
    @org.springframework.transaction.annotation.Transactional
    public int ogohlantirishYuborish(OgohlantirishRequestDTO dto) {
        List<HisobotSatriDTO> pastlar = pastOzlashtiruvchilar(
                dto.getOquvYiliId(), dto.getSemestr(), dto.getKursId(), dto.getGuruhId(), dto.getFanId());

        if (pastlar.isEmpty()) {
            return 0;
        }

        Map<UUID, List<HisobotSatriDTO>> studentBoyicha = pastlar.stream()
                .collect(Collectors.groupingBy(HisobotSatriDTO::getStudentId, LinkedHashMap::new, Collectors.toList()));

        int yuborildi = 0;
        for (Map.Entry<UUID, List<HisobotSatriDTO>> entry : studentBoyicha.entrySet()) {
            List<HisobotSatriDTO> fanlar = entry.getValue();
            String fanlarMatni = fanlar.stream()
                    .map(f -> f.getFanNomi() + " (" + f.getSemestrBahosi() + ")")
                    .collect(Collectors.joining(", "));

            XabarCreateDTO xabar = new XabarCreateDTO();
            xabar.setSenderId(String.valueOf(dto.getSenderId()));
            xabar.setSenderType("USER");
            xabar.setReceiverId(entry.getKey().toString());
            xabar.setReceiverType("STUDENT");
            xabar.setMavzu("O'zlashtirish darajasi bo'yicha ogohlantirish");
            xabar.setMazmun("Hurmatli " + fanlar.get(0).getStudentFio()
                    + ", quyidagi fan(lar) bo'yicha o'zlashtirish darajangiz past: " + fanlarMatni
                    + ". Iltimos, choralar ko'ring va zarur bo'lsa o'qituvchi bilan bog'laning.");

            xabarService.send(xabar);
            yuborildi++;
        }
        return yuborildi;
    }

    // ===== Yordamchi metodlar =====

    private List<Group> aniqlaGuruhlar(UUID kursId, UUID guruhId) {
        if (guruhId != null) {
            Group g = groupRepository.findById(guruhId)
                    .orElseThrow(() -> new NotFoundException("Guruh topilmadi: " + guruhId));
            return List.of(g);
        } else if (kursId != null) {
            return groupRepository.findByCourseId(kursId);
        } else {
            return groupRepository.findKursantGuruhlari();
        }
    }

    private HisobotSatriDTO qatorYasash(UUID studentId, String studentFio, String reytingDaftarchasiRaqami,
                                        String kursNomi, String guruhNomi,
                                        ElektronJurnalService.StudentFanNatijasi natija) {
        Double joriy = ortacha(natija.rkb1(), natija.rkb2());
        Double oraliq = natija.ronSem();
        Double yakuniy = natija.rynEffektiv() != null ? natija.rynEffektiv().doubleValue() : null;
        // Semestr bahosi - qayta hisoblanmaydi, elektron jurnaldagi haqiqiy R(SEM)
        Double semestrBahosi = natija.rsem();

        return HisobotSatriDTO.builder()
                .studentId(studentId)
                .studentFio(studentFio)
                .reytingDaftarchasiRaqami(reytingDaftarchasiRaqami)
                .kursNomi(kursNomi)
                .guruhNomi(guruhNomi)
                .fanNomi(natija.fanNomi())
                .joriyBaho(joriy)
                .oraliqBaho(oraliq)
                .yakuniyBaho(yakuniy)
                .semestrBahosi(semestrBahosi)
                .ozlashtirishDarajasi(darajaAniqlash(semestrBahosi))
                .build();
    }

    private HisobotSatriDTO qatorYasashJurnaldan(ElektronJurnalResponseDTO.KursantJurnalDTO kj,
                                                 Map<UUID, String> raqamlar,
                                                 String kursNomi, String guruhNomi, String fanNomi) {
        Double joriy = ortacha(kj.getRkb1(), kj.getRkb2());
        Double oraliq = kj.getRonSem();
        Integer yakuniyEff = kj.getRynQaytaTopshirishBaho() != null ? kj.getRynQaytaTopshirishBaho() : kj.getRyn();
        Double yakuniy = yakuniyEff != null ? yakuniyEff.doubleValue() : null;
        // Semestr bahosi - qayta hisoblanmaydi, elektron jurnaldagi haqiqiy R(SEM)
        Double semestrBahosi = kj.getRsem();

        return HisobotSatriDTO.builder()
                .studentId(kj.getStudentId())
                .studentFio(kj.getStudentFio())
                .reytingDaftarchasiRaqami(raqamlar.get(kj.getStudentId()))
                .kursNomi(kursNomi)
                .guruhNomi(guruhNomi)
                .fanNomi(fanNomi)
                .joriyBaho(joriy)
                .oraliqBaho(oraliq)
                .yakuniyBaho(yakuniy)
                .semestrBahosi(semestrBahosi)
                .ozlashtirishDarajasi(darajaAniqlash(semestrBahosi))
                .build();
    }

    // Ko'p kursantning reyting daftarchasi raqamlarini BITTA so'rovda olish (N+1 oldini olish)
    private Map<UUID, String> raqamlarniOl(List<UUID> studentIds) {
        if (studentIds.isEmpty()) return Map.of();
        return studentRepository.findAllById(studentIds).stream()
                .collect(Collectors.toMap(Student::getId, s ->
                        s.getReytingDaftarchasiRaqami() != null ? s.getReytingDaftarchasiRaqami() : ""));
    }

    // R(KB1)/R(KB2) lar o'rtachasi (mavjudlari bo'yicha) - "Joriy baho"
    private Double ortacha(Double a, Double b) {
        if (a == null && b == null) return null;
        if (a == null) return yaxlaBaho(b);
        if (b == null) return yaxlaBaho(a);
        return yaxlaBaho((a + b) / 2);
    }

    private Double ortachaRoyxat(List<Double> royxat) {
        if (royxat == null || royxat.isEmpty()) return null;
        double sum = royxat.stream().mapToDouble(Double::doubleValue).sum();
        return Math.round((sum / royxat.size()) * 10.0) / 10.0;
    }

    // O'zlashtirish darajasi: >=4.5 A'lo, >=3.5 Yaxshi, >=3.0 Qoniqarli, aks holda Past
    private String darajaAniqlash(Double baho) {
        if (baho == null) return null;
        if (baho >= 4.5) return "A'lo";
        if (baho >= 3.5) return "Yaxshi";
        if (baho >= 3.0) return "Qoniqarli";
        return "Past";
    }

    // Maxsus yaxlitlash qoidasi (ElektronJurnalService dagi bilan bir xil):
    // kasr qismi 0.51 dan katta yoki teng bo'lsa yuqoriga, aks holda pastga
    private Double yaxlaBaho(Double qiymat) {
        if (qiymat == null) return null;
        long butun = (long) Math.floor(qiymat);
        double kasr = qiymat - butun;
        return kasr >= 0.51 ? (double) (butun + 1) : (double) butun;
    }
}