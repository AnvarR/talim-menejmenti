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

        // Barcha darslar (sana filtersiz — frontend semestr bo'yicha ajratadi)
        List<DarsJurnali> darslar = darsJurnaliRepository
                .findByOqituvchiFanTaqsimlashIdAndDarsTuriAndOquvYiliId(
                        oqituvchiFanTaqsimlashId, darsTuri, oquvYiliId)
                .stream()
                .sorted(Comparator.comparing(DarsJurnali::getSana))
                .collect(Collectors.toList());

        // O'quv yili davomida barcha sanalar (R(KB) hisoblash uchun)
        LocalDate oquvYiliBoshlanish = LocalDate.of(oquvYili.getBoshlanishYil(), 9, 1);
        LocalDate oquvYiliTugash = LocalDate.of(oquvYili.getTugashYil(), 6, 30);

        // Guruhidagi barcha kursantlar (alifbo tartibida)
        List<Student> kursantlar = new ArrayList<>();
        if (taqsimlash.getGuruhlar() != null) {
            for (var guruh : taqsimlash.getGuruhlar()) {
                kursantlar.addAll(studentRepository.findByGroupIdOrderByFioAsc(guruh.getId()));
            }
        }

        // Oraliq nazorat baholari (shu semestr)
        List<OraliqNazorat> oraliqlar = oraliqNazoratRepository
                .findByOqituvchiFanTaqsimlashIdAndOquvYiliIdAndSemestr(
                        oqituvchiFanTaqsimlashId, oquvYiliId, semestr);

        // Yakuniy nazorat baholari
        List<YakuniyNazorat> yakuniylar = yakuniyNazoratRepository
                .findByOqituvchiFanTaqsimlashIdAndOquvYiliId(
                        oqituvchiFanTaqsimlashId, oquvYiliId);

        // 1-semestr oraliq nazorat baholari (R(ON.SEM) uchun)
        List<OraliqNazorat> birinchiSemestrOraliqlar = oraliqNazoratRepository
                .findByOqituvchiFanTaqsimlashIdAndOquvYiliIdAndSemestr(
                        oqituvchiFanTaqsimlashId, oquvYiliId, Semestr.BIRINCHI);

        // Kursantlar uchun jadval yaratish
        List<ElektronJurnalResponseDTO.KursantJurnalDTO> kursantJurnallar =
                kursantlar.stream()
                        .map(student -> buildKursantJurnal(
                                student, darslar, oraliqlar, birinchiSemestrOraliqlar,
                                yakuniylar, oqituvchiFanTaqsimlashId, oquvYiliId,
                                semestr, oquvYiliBoshlanish, oquvYiliTugash))
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
                                              DarsTuri darsTuri, LocalDate sana) {

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

        if (baho != null) {
            if (baho < 3 || baho > 5) {
                throw new RuntimeException("Baho 3, 4 yoki 5 bo'lishi kerak!");
            }
            // Agar avval holat bo'lsa (N/K/S/Y) — qayta topshirish bahosi
            if (davomat.getHolat() != null) {
                davomat.setQaytaTopshirishBaho(baho);
            } else {
                davomat.setBaho(baho);
            }
        }

        if (holat != null) {
            davomat.setHolat(holat);
        }

        return toAmaliyDavomatDTO(amaliyDavomatRepository.save(davomat));
    }

    // Oraliq nazorat bahosini kiritish/yangilash
    @Transactional
    public void oraliqNazoratYangilash(Long oqituvchiFanTaqsimlashId,
                                       Long studentId, Long oquvYiliId,
                                       Semestr semestr, Integer baho) {
        if (baho < 3 || baho > 5) {
            throw new RuntimeException("Baho 3, 4 yoki 5 bo'lishi kerak!");
        }

        OraliqNazorat nazorat = oraliqNazoratRepository
                .findByOqituvchiFanTaqsimlashIdAndStudentIdAndOquvYiliIdAndSemestr(
                        oqituvchiFanTaqsimlashId, studentId, oquvYiliId, semestr)
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
                    .ronBaho(baho)
                    .build();
        } else {
            nazorat.setRonBaho(baho);
        }

        oraliqNazoratRepository.save(nazorat);
    }

    // Yakuniy nazorat bahosini kiritish/yangilash
    @Transactional
    public void yakuniyNazoratYangilash(Long oqituvchiFanTaqsimlashId,
                                        Long studentId, Long oquvYiliId,
                                        Integer baho) {
        if (baho < 3 || baho > 5) {
            throw new RuntimeException("Baho 3, 4 yoki 5 bo'lishi kerak!");
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

    private ElektronJurnalResponseDTO.KursantJurnalDTO buildKursantJurnal(
            Student student,
            List<DarsJurnali> darslar,
            List<OraliqNazorat> oraliqlar,
            List<OraliqNazorat> birinchiSemestrOraliqlar,
            List<YakuniyNazorat> yakuniylar,
            Long taqsimlashId,
            Long oquvYiliId,
            Semestr semestr,
            LocalDate boshlanish,
            LocalDate tugash) {

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

        // R(KB) — kunlik baholar o'rtachasi (butun o'quv yili davomida)
        List<AmaliyDavomat> baholangan = amaliyDavomatRepository
                .findBaholangan(student.getId(), taqsimlashId, boshlanish, tugash);
        Double rkb = null;
        if (!baholangan.isEmpty()) {
            double sum = baholangan.stream()
                    .mapToInt(d -> d.getBaho() != null ? d.getBaho() :
                            (d.getQaytaTopshirishBaho() != null ?
                                    d.getQaytaTopshirishBaho() : 0))
                    .sum();
            rkb = Math.round((sum / baholangan.size()) * 100.0) / 100.0;
        }

        // R(ON) — oraliq nazorat bahosi
        Integer ron = oraliqlar.stream()
                .filter(o -> o.getStudent().getId().equals(student.getId()))
                .findFirst()
                .map(OraliqNazorat::getRonBaho)
                .orElse(null);

        // R(MT) — hozircha null
        Double rmt = null;

        // R(1ON) yoki R(2ON) = (R(KB) + R(ON) + R(MT)) / hisobga kiritilganlar soni
        Double r1on = null;
        if (rkb != null || ron != null) {
            double sum = 0;
            int count = 0;
            if (rkb != null) { sum += rkb; count++; }
            if (ron != null) { sum += ron; count++; }
            if (rmt != null) { sum += rmt; count++; }
            r1on = count > 0 ? Math.round((sum / count) * 100.0) / 100.0 : null;
        }

        // R(ON.SEM) — faqat 2-semestr uchun (R(1ON) + R(2ON)) / 2
        Double ronSem = null;
        if (semestr == Semestr.IKKINCHI) {
            Integer ron1 = birinchiSemestrOraliqlar.stream()
                    .filter(o -> o.getStudent().getId().equals(student.getId()))
                    .findFirst()
                    .map(OraliqNazorat::getRonBaho)
                    .orElse(null);

            if (r1on != null && ron1 != null) {
                ronSem = Math.round(((r1on + ron1) / 2) * 100.0) / 100.0;
            }
        }

        // R(YN) — yakuniy nazorat bahosi
        Integer ryn = yakuniylar.stream()
                .filter(y -> y.getStudent().getId().equals(student.getId()))
                .findFirst()
                .map(YakuniyNazorat::getYnBaho)
                .orElse(null);

        // R(SEM) = (R(ON.SEM) + R(YN)) / 2
        Double rsem = null;
        if (ronSem != null && ryn != null) {
            rsem = Math.round(((ronSem + ryn) / 2) * 100.0) / 100.0;
        }

        return ElektronJurnalResponseDTO.KursantJurnalDTO.builder()
                .studentId(student.getId())
                .studentFio(student.getFio())
                .davomatlar(davomatlar)
                .rkb(rkb)
                .ron(ron)
                .rmt(rmt)
                .r1on(r1on)
                .ronSem(ronSem)
                .ryn(ryn)
                .rsem(rsem)
                .build();
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