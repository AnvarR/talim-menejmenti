package com.edu.talim.service;

import com.edu.talim.dto.AmaliyDavomatResponseDTO;
import com.edu.talim.dto.DarsJurnaliResponseDTO;
import com.edu.talim.dto.ElektronJurnalResponseDTO;
import com.edu.talim.entity.*;
import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.entity.enums.DavomatHolati;
import com.edu.talim.entity.enums.Semestr;
import com.edu.talim.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElektronJurnalService {

    private final DarsJurnaliRepository darsJurnaliRepository;
    private final AmaliyDavomatRepository amaliyDavomatRepository;
    private final OraliqNazoratRepository oraliqNazoratRepository;
    private final YakuniyNazoratRepository yakuniyNazoratRepository;
    private final OqituvchiFanTaqsimlashRepository oqituvchiFanTaqsimlashRepository;
    private final OquvYiliRepository oquvYiliRepository;
    private final StudentRepository studentRepository;
    private final SutkalikNaryadRepository sutkalikNaryadRepository;
    private final KasalRepository kasalRepository;
    private final EntityManager entityManager;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    // Elektron jurnal — to'liq jadval
    public ElektronJurnalResponseDTO getJurnal(Long oqituvchiFanTaqsimlashId,
                                               DarsTuri darsTuri,
                                               Semestr semestr,
                                               Long oquvYiliId) {

        OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new RuntimeException("Fan taqsimlash topilmadi"));

        OquvYili oquvYili = oquvYiliRepository.findById(oquvYiliId)
                .orElseThrow(() -> new RuntimeException("O'quv yili topilmadi"));

        // Shu semestrga tegishli darslar
        List<DarsJurnali> darslar = darsJurnaliRepository
                .findByOqituvchiFanTaqsimlashIdAndDarsTuriAndOquvYiliIdAndSemestr(
                        oqituvchiFanTaqsimlashId, darsTuri, oquvYiliId, semestr)
                .stream()
                .sorted(Comparator.comparing(DarsJurnali::getSana))
                .collect(Collectors.toList());

        // O'quv yili boshlanish sanasi (R(KB) ning eng birinchi hisoblash nuqtasi)
        LocalDate oquvYiliBoshlanish = LocalDate.of(oquvYili.getBoshlanishYil(), 9, 1);

        // Guruhidagi barcha kursantlar (alifbo tartibida)
        List<Student> kursantlar = new ArrayList<>();
        if (taqsimlash.getGuruhlar() != null) {
            for (var guruh : taqsimlash.getGuruhlar()) {
                kursantlar.addAll(studentRepository.findByGroupIdOrderByFioAsc(guruh.getId()));
            }
        }

        // Shu semestrning 1- va 2-oraliq nazorat baholari
        List<OraliqNazorat> oraliq1lar = oraliqNazoratRepository
                .findByOqituvchiFanTaqsimlashIdAndOquvYiliIdAndSemestrAndOraliqRaqami(
                        oqituvchiFanTaqsimlashId, oquvYiliId, semestr, 1);
        List<OraliqNazorat> oraliq2lar = oraliqNazoratRepository
                .findByOqituvchiFanTaqsimlashIdAndOquvYiliIdAndSemestrAndOraliqRaqami(
                        oqituvchiFanTaqsimlashId, oquvYiliId, semestr, 2);

        // Butun o'quv yili davomidagi barcha oraliqlar (ikkala semestr) —
        // R(KB) uchun "oldingi kesim sanasi"ni aniqlashda kerak bo'ladi
        List<OraliqNazorat> yilOraliqlari = oraliqNazoratRepository
                .findByOqituvchiFanTaqsimlashIdAndOquvYiliId(oqituvchiFanTaqsimlashId, oquvYiliId);

        // Yakuniy nazorat baholari
        List<YakuniyNazorat> yakuniylar = yakuniyNazoratRepository
                .findByOqituvchiFanTaqsimlashIdAndOquvYiliId(
                        oqituvchiFanTaqsimlashId, oquvYiliId);

        // Kursantlar uchun jadval yaratish
        List<ElektronJurnalResponseDTO.KursantJurnalDTO> kursantJurnallar =
                kursantlar.stream()
                        .map(student -> buildKursantJurnal(
                                student, darslar, oraliq1lar, oraliq2lar, yilOraliqlari,
                                yakuniylar, oqituvchiFanTaqsimlashId,
                                oquvYiliBoshlanish))
                        .collect(Collectors.toList());

        // Guruh nomini birlashtirish
        String guruhNomi = taqsimlash.getGuruhlar() != null ?
                taqsimlash.getGuruhlar().stream()
                        .map(Group::getGuruhNomi)
                        .collect(Collectors.joining(", ")) : null;

        return ElektronJurnalResponseDTO.builder()
                .oqituvchiFanTaqsimlashId(oqituvchiFanTaqsimlashId)
                .fanNomi(taqsimlash.getFanTaqsimlash().getFan().getFanNomi())
                .oqituvchiFio(taqsimlash.getOqituvchi().getFio())
                .kursNomi(taqsimlash.getKurs().getKursRaqami() + "-kurs")
                .guruhNomi(guruhNomi)
                .oquvYiliNomi(oquvYili.getNom())
                .darsTuri(darsTuri)
                .semestr(semestr)
                .darslar(darslar.stream()
                        .map(d -> toDarsDTO(d, taqsimlash, oquvYili))
                        .collect(Collectors.toList()))
                .kursantlar(kursantJurnallar)
                .build();
    }

    // Yangi dars qo'shish (sana tanlanganda)
    @Transactional
    public DarsJurnaliResponseDTO darsQoshish(Long oqituvchiFanTaqsimlashId,
                                              DarsTuri darsTuri, Semestr semestr, LocalDate sana) {

        OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new RuntimeException("Fan taqsimlash topilmadi"));

        OquvYili faolYil = oquvYiliRepository.findByFaolTrue()
                .orElseThrow(() -> new RuntimeException("Faol o'quv yili topilmadi"));

        darsJurnaliRepository.findByOqituvchiFanTaqsimlashIdAndDarsTuriAndSana(
                        oqituvchiFanTaqsimlashId, darsTuri, sana)
                .ifPresent(d -> {
                    throw new RuntimeException("Bu sana uchun dars allaqachon mavjud: " + sana);
                });

        DarsJurnali darsJurnali = DarsJurnali.builder()
                .oqituvchiFanTaqsimlash(taqsimlash)
                .oquvYili(faolYil)
                .darsTuri(darsTuri)
                .semestr(semestr)
                .sana(sana)
                .soat(2)
                .build();

        darsJurnali = darsJurnaliRepository.save(darsJurnali);

        // Guruhidagi kursantlar uchun amaliy davomat yaratish
        yaratAmaliyDavomatlar(darsJurnali, taqsimlash, sana);

        darsJurnaliRepository.flush();
        entityManager.refresh(darsJurnali);

        return toDarsDTO(darsJurnali, taqsimlash, faolYil);
    }

    // Dars sanasini o'zgartirish (cheklovsiz — istalgan vaqtda)
    @Transactional
    public DarsJurnaliResponseDTO darsSanasiniOzgartirish(Long darsJurnaliId, LocalDate yangiSana) {
        DarsJurnali darsJurnali = darsJurnaliRepository.findById(darsJurnaliId)
                .orElseThrow(() -> new RuntimeException("Dars topilmadi: " + darsJurnaliId));

        darsJurnaliRepository.findByOqituvchiFanTaqsimlashIdAndDarsTuriAndSana(
                        darsJurnali.getOqituvchiFanTaqsimlash().getId(),
                        darsJurnali.getDarsTuri(), yangiSana)
                .filter(d -> !d.getId().equals(darsJurnaliId))
                .ifPresent(d -> {
                    throw new RuntimeException("Bu sana uchun dars allaqachon mavjud: " + yangiSana);
                });

        darsJurnali.setSana(yangiSana);
        darsJurnali = darsJurnaliRepository.save(darsJurnali);

        return toDarsDTO(darsJurnali, darsJurnali.getOqituvchiFanTaqsimlash(), darsJurnali.getOquvYili());
    }

    // Darsni o'chirish (cheklovsiz — istalgan vaqtda), bog'liq davomatlar bilan birga
    @Transactional
    public void darsniOchirish(Long darsJurnaliId) {
        DarsJurnali darsJurnali = darsJurnaliRepository.findById(darsJurnaliId)
                .orElseThrow(() -> new RuntimeException("Dars topilmadi: " + darsJurnaliId));

        List<AmaliyDavomat> davomatlar = amaliyDavomatRepository
                .findByDarsJurnaliIdOrderByStudentFioAsc(darsJurnaliId);
        amaliyDavomatRepository.deleteAll(davomatlar);

        darsJurnaliRepository.delete(darsJurnali);
    }

    // Davomat/baho yangilash
    @Transactional
    public AmaliyDavomatResponseDTO davomatYangilash(Long davomatId,
                                                     DavomatHolati holat,
                                                     Integer baho) {
        AmaliyDavomat davomat = amaliyDavomatRepository.findById(davomatId)
                .orElseThrow(() -> new RuntimeException("Davomat topilmadi: " + davomatId));

        if (davomat.getBloklanganMi()) {
            throw new RuntimeException("Kursant bloklangan! Dekanat ruxsati kerak.");
        }

        // Qayta topshirish bosqichimi: kursant avval qatnashmagan (N/K/S/Y)
        // YOKI darsni o'zlashtirmasdan eng past baho (2) olgan bo'lsa
        boolean qaytaTopshirishBosqichi = davomat.getHolat() != null
                || (davomat.getBaho() != null && davomat.getBaho() == 2);

        if (qaytaTopshirishBosqichi) {
            // Bu bosqichda faqat baho (2/3/4/5) orqali amalga oshiriladi, holat qayta o'rnatilmaydi
            if (holat != null) {
                throw new RuntimeException(
                        "Bu amalni amalga oshirolmaysiz! Qayta topshirish faqat baho (2, 3, 4 yoki 5) qo'yish orqali amalga oshiriladi.");
            }
            if (baho == null) {
                throw new RuntimeException("Qayta topshirish uchun baho (2, 3, 4 yoki 5) kiriting!");
            }
            if (baho < 2 || baho > 5) {
                throw new RuntimeException("Baho 2, 3, 4 yoki 5 bo'lishi kerak!");
            }
            davomat.setQaytaTopshirishBaho(baho);
        } else {
            if (holat != null) {
                davomat.setHolat(holat);
            }
            if (baho != null) {
                if (baho < 2 || baho > 5) {
                    throw new RuntimeException("Baho 2, 3, 4 yoki 5 bo'lishi kerak!");
                }
                davomat.setBaho(baho);
            }
        }

        return toAmaliyDavomatDTO(amaliyDavomatRepository.save(davomat));
    }

    // Oraliq nazorat bahosini kiritish/yangilash (oraliqRaqami: 1 yoki 2, kesimSanasi — o'qituvchi belgilaydi)
    @Transactional
    public void oraliqNazoratYangilash(Long oqituvchiFanTaqsimlashId,
                                       Long studentId, Long oquvYiliId,
                                       Semestr semestr, Integer oraliqRaqami,
                                       LocalDate kesimSanasi, Integer baho) {
        if (baho < 2 || baho > 5) {
            throw new RuntimeException("Baho 2, 3, 4 yoki 5 bo'lishi kerak!");
        }
        if (oraliqRaqami == null || (oraliqRaqami != 1 && oraliqRaqami != 2)) {
            throw new RuntimeException("Oraliq raqami 1 yoki 2 bo'lishi kerak!");
        }
        if (kesimSanasi == null) {
            throw new RuntimeException("Kesim sanasi kiritilishi shart!");
        }

        OraliqNazorat nazorat = oraliqNazoratRepository
                .findByOqituvchiFanTaqsimlashIdAndStudentIdAndOquvYiliIdAndSemestrAndOraliqRaqami(
                        oqituvchiFanTaqsimlashId, studentId, oquvYiliId, semestr, oraliqRaqami)
                .orElse(null);

        if (nazorat == null) {
            OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                    .findById(oqituvchiFanTaqsimlashId)
                    .orElseThrow(() -> new RuntimeException("Fan taqsimlash topilmadi"));
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Kursant topilmadi"));
            OquvYili oquvYili = oquvYiliRepository.findById(oquvYiliId)
                    .orElseThrow(() -> new RuntimeException("O'quv yili topilmadi"));

            nazorat = OraliqNazorat.builder()
                    .oqituvchiFanTaqsimlash(taqsimlash)
                    .student(student)
                    .oquvYili(oquvYili)
                    .semestr(semestr)
                    .oraliqRaqami(oraliqRaqami)
                    .kesimSanasi(kesimSanasi)
                    .ronBaho(baho)
                    .build();
        } else {
            nazorat.setKesimSanasi(kesimSanasi);
            nazorat.setRonBaho(baho);
        }

        oraliqNazoratRepository.save(nazorat);
    }

    // Yakuniy nazorat bahosini kiritish/yangilash
    @Transactional
    public void yakuniyNazoratYangilash(Long oqituvchiFanTaqsimlashId,
                                        Long studentId, Long oquvYiliId,
                                        Integer baho) {
        if (baho < 2 || baho > 5) {
            throw new RuntimeException("Baho 2, 3, 4 yoki 5 bo'lishi kerak!");
        }

        YakuniyNazorat nazorat = yakuniyNazoratRepository
                .findByOqituvchiFanTaqsimlashIdAndStudentIdAndOquvYiliId(
                        oqituvchiFanTaqsimlashId, studentId, oquvYiliId)
                .orElse(null);

        if (nazorat == null) {
            OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                    .findById(oqituvchiFanTaqsimlashId)
                    .orElseThrow(() -> new RuntimeException("Fan taqsimlash topilmadi"));
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Kursant topilmadi"));
            OquvYili oquvYili = oquvYiliRepository.findById(oquvYiliId)
                    .orElseThrow(() -> new RuntimeException("O'quv yili topilmadi"));

            nazorat = YakuniyNazorat.builder()
                    .oqituvchiFanTaqsimlash(taqsimlash)
                    .student(student)
                    .oquvYili(oquvYili)
                    .ynBaho(baho)
                    .build();
        } else {
            nazorat.setYnBaho(baho);
        }

        yakuniyNazoratRepository.save(nazorat);
    }

    // ====================== Yordamchi metodlar ======================

    // Maxsus yaxlitlash qoidasi: kasr qismi 0.51 dan katta yoki teng bo'lsa yuqoriga,
    // aks holda pastga yaxlitlanadi (masalan 4.51->5, 4.50->4; 3.51->4, 3.50->3).
    private Double yaxlaBaho(Double qiymat) {
        if (qiymat == null) return null;
        long butun = (long) Math.floor(qiymat);
        double kasr = qiymat - butun;
        return kasr >= 0.51 ? (double) (butun + 1) : (double) butun;
    }

    // Kursant bo'yicha shu oraliqdan oldingi eng yaqin kesim sanasini topadi
    // (yo'q bo'lsa — o'quv yili boshlanish sanasi qaytariladi)
    private LocalDate oldingiKesimSanasi(List<OraliqNazorat> studentBoyichaOraliqlar,
                                         LocalDate joriyKesim,
                                         LocalDate oquvYiliBoshlanish) {
        return studentBoyichaOraliqlar.stream()
                .map(OraliqNazorat::getKesimSanasi)
                .filter(sana -> sana != null && sana.isBefore(joriyKesim))
                .max(LocalDate::compareTo)
                .orElse(oquvYiliBoshlanish);
    }

    // R(KB) — berilgan sana oralig'idagi kunlik baholarning yaxlitlangan o'rtachasi
    private Double hisoblaRkb(Long studentId, Long taqsimlashId,
                              LocalDate boshlanish, LocalDate tugash) {
        if (boshlanish == null || tugash == null || boshlanish.isAfter(tugash)) return null;
        List<AmaliyDavomat> baholangan = amaliyDavomatRepository
                .findBaholangan(studentId, taqsimlashId, boshlanish, tugash);
        if (baholangan.isEmpty()) return null;
        double sum = baholangan.stream()
                .mapToInt(d -> d.getQaytaTopshirishBaho() != null ? d.getQaytaTopshirishBaho() :
                        (d.getBaho() != null ? d.getBaho() : 0))
                .sum();
        return yaxlaBaho(sum / baholangan.size());
    }

    private ElektronJurnalResponseDTO.KursantJurnalDTO buildKursantJurnal(
            Student student,
            List<DarsJurnali> darslar,
            List<OraliqNazorat> oraliq1lar,
            List<OraliqNazorat> oraliq2lar,
            List<OraliqNazorat> yilOraliqlari,
            List<YakuniyNazorat> yakuniylar,
            Long taqsimlashId,
            LocalDate oquvYiliBoshlanish) {

        // Kursantning davomatlari (har bir dars uchun)
        List<AmaliyDavomatResponseDTO> davomatlar = darslar.stream()
                .map(dars -> amaliyDavomatRepository
                        .findByDarsJurnaliIdAndStudentId(dars.getId(), student.getId())
                        .map(this::toAmaliyDavomatDTO)
                        .orElse(AmaliyDavomatResponseDTO.builder()
                                .studentId(student.getId())
                                .studentFio(student.getFio())
                                .build()))
                .collect(Collectors.toList());

        // Shu kursantga tegishli barcha oraliq nazorat yozuvlari (butun yil bo'yicha)
        List<OraliqNazorat> mendagiYilOraliqlari = yilOraliqlari.stream()
                .filter(o -> o.getStudent().getId().equals(student.getId()))
                .collect(Collectors.toList());

        // ===== 1-ORALIQ =====
        OraliqNazorat oraliq1 = oraliq1lar.stream()
                .filter(o -> o.getStudent().getId().equals(student.getId()))
                .findFirst().orElse(null);

        Double rkb1 = null;
        Integer ron1 = null;
        Double rmt1 = null; // hozircha null
        Double r1on = null;
        LocalDate kesim1Sanasi = null;

        if (oraliq1 != null) {
            kesim1Sanasi = oraliq1.getKesimSanasi();
            ron1 = oraliq1.getRonBaho();
            LocalDate boshlanish1 = oldingiKesimSanasi(mendagiYilOraliqlari, kesim1Sanasi, oquvYiliBoshlanish);
            rkb1 = hisoblaRkb(student.getId(), taqsimlashId, boshlanish1, kesim1Sanasi);
            r1on = hisoblaOraliqNatija(rkb1, ron1, rmt1);
        }

        // ===== 2-ORALIQ =====
        OraliqNazorat oraliq2 = oraliq2lar.stream()
                .filter(o -> o.getStudent().getId().equals(student.getId()))
                .findFirst().orElse(null);

        Double rkb2 = null;
        Integer ron2 = null;
        Double rmt2 = null; // hozircha null
        Double r2on = null;
        LocalDate kesim2Sanasi = null;

        if (oraliq2 != null) {
            kesim2Sanasi = oraliq2.getKesimSanasi();
            ron2 = oraliq2.getRonBaho();
            LocalDate boshlanish2 = oldingiKesimSanasi(mendagiYilOraliqlari, kesim2Sanasi, oquvYiliBoshlanish);
            rkb2 = hisoblaRkb(student.getId(), taqsimlashId, boshlanish2, kesim2Sanasi);
            r2on = hisoblaOraliqNatija(rkb2, ron2, rmt2);
        }

        // R(ON.SEM) = (R(1ON)+R(2ON))/2
        Double ronSem = null;
        if (r1on != null && r2on != null) {
            ronSem = yaxlaBaho((r1on + r2on) / 2);
        }

        // R(YN) — yakuniy nazorat bahosi
        Integer ryn = yakuniylar.stream()
                .filter(y -> y.getStudent().getId().equals(student.getId()))
                .findFirst()
                .map(YakuniyNazorat::getYnBaho)
                .orElse(null);

        // R(SEM) = (R(ON.SEM)+R(YN))/2
        Double rsem = null;
        if (ronSem != null && ryn != null) {
            rsem = yaxlaBaho((ronSem + ryn) / 2);
        }

        return ElektronJurnalResponseDTO.KursantJurnalDTO.builder()
                .studentId(student.getId())
                .studentFio(student.getFio())
                .davomatlar(davomatlar)
                .rkb1(rkb1)
                .ron1(ron1)
                .rmt1(rmt1)
                .r1on(r1on)
                .kesim1Sanasi(kesim1Sanasi)
                .rkb2(rkb2)
                .ron2(ron2)
                .rmt2(rmt2)
                .r2on(r2on)
                .kesim2Sanasi(kesim2Sanasi)
                .ronSem(ronSem)
                .ryn(ryn)
                .rsem(rsem)
                .build();
    }

    // R(1ON) yoki R(2ON) = mavjud komponentlar (R(KB), R(ON), R(MT)) o'rtachasi, yaxlitlangan
    private Double hisoblaOraliqNatija(Double rkb, Integer ron, Double rmt) {
        if (rkb == null && ron == null && rmt == null) return null;
        double sum = 0;
        int count = 0;
        if (rkb != null) { sum += rkb; count++; }
        if (ron != null) { sum += ron; count++; }
        if (rmt != null) { sum += rmt; count++; }
        return count > 0 ? yaxlaBaho(sum / count) : null;
    }

    private void yaratAmaliyDavomatlar(DarsJurnali darsJurnali,
                                       OqituvchiFanTaqsimlash taqsimlash,
                                       LocalDate sana) {
        List<Student> kursantlar = new ArrayList<>();
        if (taqsimlash.getGuruhlar() != null) {
            for (var guruh : taqsimlash.getGuruhlar()) {
                kursantlar.addAll(studentRepository.findByGroupIdOrderByFioAsc(guruh.getId()));
            }
        }

        for (Student student : kursantlar) {
            DavomatHolati holat = null;

            // N: sutkalik naryadda bormi?
            if (sutkalikNaryadRepository.existsByStudentIdAndQabulQilishSanasi(
                    student.getId(), sana)) {
                holat = DavomatHolati.N;
            }
            // K: kasallar ro'yxatida shu sanada bormi?
            else if (kasalRepository.existsActiveKasal(student.getId(), sana)) {
                holat = DavomatHolati.K;
            }

            // Blok tekshiruvi
            boolean bloklangan = bloklashniTekshir(student.getId(), sana);

            AmaliyDavomat davomat = AmaliyDavomat.builder()
                    .darsJurnali(darsJurnali)
                    .student(student)
                    .holat(holat)
                    .bloklanganMi(bloklangan)
                    .bloklashSanasi(bloklangan ? sana : null)
                    .build();

            amaliyDavomatRepository.save(davomat);
        }
    }

    // Blok tekshiruvi: 7 kundan oshgan va qayta topshirilmagan
    private boolean bloklashniTekshir(Long studentId, LocalDate sana) {
        LocalDate yettaKunOldin = sana.minusDays(7);
        List<AmaliyDavomat> bloklashKeraklar = amaliyDavomatRepository
                .findBloklashKeraklar(studentId, yettaKunOldin);
        return !bloklashKeraklar.isEmpty();
    }

    private DarsJurnaliResponseDTO toDarsDTO(DarsJurnali entity,
                                             OqituvchiFanTaqsimlash taqsimlash,
                                             OquvYili oquvYili) {
        String guruhNomi = taqsimlash.getGuruhlar() != null ?
                taqsimlash.getGuruhlar().stream()
                        .map(Group::getGuruhNomi)
                        .collect(Collectors.joining(", ")) : null;

        return DarsJurnaliResponseDTO.builder()
                .id(entity.getId())
                .oqituvchiFanTaqsimlashId(taqsimlash.getId())
                .fanNomi(taqsimlash.getFanTaqsimlash().getFan().getFanNomi())
                .oqituvchiFio(taqsimlash.getOqituvchi().getFio())
                .kursNomi(taqsimlash.getKurs().getKursRaqami() + "-kurs")
                .guruhNomi(guruhNomi)
                .oquvYiliId(oquvYili.getId())
                .oquvYiliNomi(oquvYili.getNom())
                .darsTuri(entity.getDarsTuri())
                .semestr(entity.getSemestr())
                .sana(entity.getSana())
                .soat(entity.getSoat())
                .mavzuNomi(entity.getMavzuNomi())
                .topshiriqFaylNomi(entity.getTopshiriqFaylNomi())
                .topshiriqFaylUrl(entity.getTopshiriqFaylYoli() != null ?
                        baseUrl + "/uploads/" + entity.getTopshiriqFaylYoli() : null)
                .davomatlar(List.of())
                .build();
    }

    private AmaliyDavomatResponseDTO toAmaliyDavomatDTO(AmaliyDavomat d) {
        return AmaliyDavomatResponseDTO.builder()
                .id(d.getId())
                .studentId(d.getStudent().getId())
                .studentFio(d.getStudent().getFio())
                .holat(d.getHolat())
                .baho(d.getBaho())
                .qaytaTopshirishBaho(d.getQaytaTopshirishBaho())
                .bloklanganMi(d.getBloklanganMi())
                .build();
    }
}