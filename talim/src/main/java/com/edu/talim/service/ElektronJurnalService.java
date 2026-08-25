package com.edu.talim.service;

import java.util.UUID;

import com.edu.talim.exception.ConflictException;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.AmaliyDavomatResponseDTO;
import com.edu.talim.dto.DarsJurnaliResponseDTO;
import com.edu.talim.dto.ElektronJurnalResponseDTO;
import com.edu.talim.entity.*;
import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.entity.enums.DavomatHolati;
import com.edu.talim.entity.enums.KochirishTuri;
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

    // SQL "date" turi uchun xavfsiz, uzoq kelajakdagi sana (LocalDate.MAX ishlatilmaydi -
    // u SQL date chegarasidan oshib, "date out of range" xatosiga olib keladi)
    private static final LocalDate CHEKSIZ_KELAJAK = LocalDate.of(2100, 12, 31);

    private final DarsJurnaliRepository darsJurnaliRepository;
    private final AmaliyDavomatRepository amaliyDavomatRepository;
    private final OraliqNazoratRepository oraliqNazoratRepository;
    private final YakuniyNazoratRepository yakuniyNazoratRepository;
    private final OqituvchiFanTaqsimlashRepository oqituvchiFanTaqsimlashRepository;
    private final OquvYiliRepository oquvYiliRepository;
    private final StudentRepository studentRepository;
    private final SutkalikNaryadRepository sutkalikNaryadRepository;
    private final KasalRepository kasalRepository;
    private final MustaqilTalimTopshiriqRepository mustaqilTalimTopshiriqRepository;
    private final MustaqilTalimJurnalService mustaqilTalimJurnalService;
    private final OquvYiliService oquvYiliService;
    private final KursKochirishTarixiRepository kursKochirishTarixiRepository;
    private final EntityManager entityManager;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    // Bir nechta kursant/faqat bitta kursant o'rtasida ishlatiladigan umumiy ma'lumotlar
    // (taqsimlash, darslar, oraliq/yakuniy baholar, R(MT) nomzodlari) — guruh hajmidan qat'i
    // nazar bir marta hisoblanadi
    private record JurnalKontekst(
            OqituvchiFanTaqsimlash taqsimlash,
            OquvYili oquvYili,
            List<DarsJurnali> darslar,
            LocalDate oquvYiliBoshlanish,
            List<OraliqNazorat> oraliq1lar,
            List<OraliqNazorat> oraliq2lar,
            List<OraliqNazorat> yilOraliqlari,
            List<YakuniyNazorat> yakuniylar,
            List<UUID> mtTaqsimlashCandidateIds
    ) {}

    private JurnalKontekst tayyorlaKontekst(UUID oqituvchiFanTaqsimlashId,
                                            DarsTuri darsTuri,
                                            Semestr semestr,
                                            UUID oquvYiliId) {

        OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi"));

        OquvYili oquvYili = oquvYiliRepository.findById(oquvYiliId)
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi"));

        // Shu semestrga tegishli darslar
        List<DarsJurnali> darslar = darsJurnaliRepository
                .findByOqituvchiFanTaqsimlashIdAndDarsTuriAndOquvYiliIdAndSemestr(
                        oqituvchiFanTaqsimlashId, darsTuri, oquvYiliId, semestr)
                .stream()
                .sorted(Comparator.comparing(DarsJurnali::getSana))
                .collect(Collectors.toList());

        // O'quv yili boshlanish sanasi (R(KB) ning eng birinchi hisoblash nuqtasi)
        LocalDate oquvYiliBoshlanish = LocalDate.of(oquvYili.getBoshlanishYil(), 9, 1);

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

        // R(MT) uchun "birodar" Mustaqil ta'lim taqsimlashini topish (bir xil fan+o'qituvchi+kurs+guruh)
        Set<UUID> mendagiGuruhIdlar = taqsimlash.getGuruhlar() == null ? Set.of() :
                taqsimlash.getGuruhlar().stream().map(Group::getId).collect(Collectors.toSet());
        List<OqituvchiFanTaqsimlash> mtNomzodlar = oqituvchiFanTaqsimlashRepository
                .findByFanTaqsimlashIdAndOqituvchiIdAndKursId(
                        taqsimlash.getFanTaqsimlash().getId(),
                        taqsimlash.getOqituvchi().getId(),
                        taqsimlash.getKurs().getId());

        // Dars turidan qat'i nazar - frontend topshiriqni ba'zan noto'g'ri turdagi taqsimlashga
        // yozib qo'yishi mumkinligi sababli, guruhi mos keladigan BARCHA nomzodlar ID ro'yxatini yig'amiz
        List<UUID> mtTaqsimlashCandidateIds = mtNomzodlar
                .stream()
                .filter(t -> t.getGuruhlar() != null && t.getGuruhlar().stream()
                        .anyMatch(g -> mendagiGuruhIdlar.contains(g.getId())))
                .map(OqituvchiFanTaqsimlash::getId)
                .collect(Collectors.toList());

        return new JurnalKontekst(taqsimlash, oquvYili, darslar, oquvYiliBoshlanish,
                oraliq1lar, oraliq2lar, yilOraliqlari, yakuniylar, mtTaqsimlashCandidateIds);
    }

    // Guruh (demak taqsimlash) SO'RALAYOTGAN o'quv yilida qaysi kursda bo'lganini aniqlaydi.
    // taqsimlash.kurs - taqsimlash birinchi marta yaratilgandagi ("boshlang'ich") kurs darajasi,
    // promotsiya (kursdan-kursga ko'chirish) sodir bo'lganda O'ZGARMAYDI, chunki taqsimlash
    // bir necha yil qayta ishlatilishi mumkin. Shu sababli - eski o'quv yili tanlanganda
    // eski kurs, yangi (yoki joriy) o'quv yili tanlanganda promotsiyadan keyingi kurs
    // ko'rsatilishi kerak. Buning uchun KursKochirishTarixi (kursant tarixi) dan foydalanamiz.
    private Integer effektivKursRaqami(Group guruh, Course taqsimlashKurs, OquvYili soraluvchiYil) {
        if (guruh == null || soraluvchiYil == null || soraluvchiYil.getBoshlanishYil() == null) {
            return taqsimlashKurs.getKursRaqami();
        }

        List<Student> guruhKursantlari = studentRepository.findByGroupIdOrderByFioAsc(guruh.getId());
        if (guruhKursantlari.isEmpty()) {
            return taqsimlashKurs.getKursRaqami();
        }

        // Guruh a'zolari birga (bitta so'rov ichida) ko'chirilgani uchun,
        // ixtiyoriy bitta a'zoning tarixi butun guruh uchun ham to'g'ri
        UUID vakilStudentId = guruhKursantlari.get(0).getId();

        List<KursKochirishTarixi> otishlar = kursKochirishTarixiRepository
                .findByStudentIdOrderBySanaDesc(vakilStudentId)
                .stream()
                .filter(t -> t.getTuri() == KochirishTuri.KOCHIRISH)
                .filter(t -> t.getOquvYili() != null && t.getOquvYili().getBoshlanishYil() != null
                        && t.getYangiKurs() != null)
                .sorted(Comparator.comparing(t -> t.getOquvYili().getBoshlanishYil()))
                .collect(Collectors.toList());

        int kurs = taqsimlashKurs.getKursRaqami();
        for (KursKochirishTarixi otish : otishlar) {
            if (otish.getOquvYili().getBoshlanishYil() <= soraluvchiYil.getBoshlanishYil()) {
                kurs = otish.getYangiKurs().getKursRaqami();
            } else {
                break;
            }
        }
        return kurs;
    }

    // Elektron jurnal — to'liq jadval (butun guruh uchun)
    public ElektronJurnalResponseDTO getJurnal(UUID oqituvchiFanTaqsimlashId,
                                               DarsTuri darsTuri,
                                               Semestr semestr,
                                               UUID oquvYiliId) {

        JurnalKontekst k = tayyorlaKontekst(oqituvchiFanTaqsimlashId, darsTuri, semestr, oquvYiliId);

        // Guruhidagi barcha kursantlar (alifbo tartibida)
        List<Student> kursantlar = new ArrayList<>();
        if (k.taqsimlash().getGuruhlar() != null) {
            for (var guruh : k.taqsimlash().getGuruhlar()) {
                kursantlar.addAll(studentRepository.findByGroupIdOrderByFioAsc(guruh.getId()));
            }
        }

        // Kursantlar uchun jadval yaratish
        List<ElektronJurnalResponseDTO.KursantJurnalDTO> kursantJurnallar =
                kursantlar.stream()
                        .map(student -> buildKursantJurnal(
                                student, k.darslar(), k.oraliq1lar(), k.oraliq2lar(), k.yilOraliqlari(),
                                k.yakuniylar(), oqituvchiFanTaqsimlashId,
                                k.oquvYiliBoshlanish(), k.mtTaqsimlashCandidateIds(), semestr, oquvYiliId))
                        .collect(Collectors.toList());

        // Guruh nomini birlashtirish
        String guruhNomi = k.taqsimlash().getGuruhlar() != null ?
                k.taqsimlash().getGuruhlar().stream()
                        .map(Group::getGuruhNomi)
                        .collect(Collectors.joining(", ")) : null;

        // So'ralayotgan o'quv yiliga mos kurs raqami (promotsiyalarni hisobga olgan holda)
        Group birinchiGuruh = (k.taqsimlash().getGuruhlar() != null && !k.taqsimlash().getGuruhlar().isEmpty())
                ? k.taqsimlash().getGuruhlar().get(0) : null;
        String kursNomi = effektivKursRaqami(birinchiGuruh, k.taqsimlash().getKurs(), k.oquvYili()) + "-kurs";

        return ElektronJurnalResponseDTO.builder()
                .oqituvchiFanTaqsimlashId(oqituvchiFanTaqsimlashId)
                .fanNomi(k.taqsimlash().getFanTaqsimlash().getFan().getFanNomi())
                .oqituvchiFio(k.taqsimlash().getOqituvchi().getFio())
                .kursNomi(kursNomi)
                .guruhNomi(guruhNomi)
                .oquvYiliNomi(k.oquvYili().getNom())
                .darsTuri(darsTuri)
                .semestr(semestr)
                .darslar(k.darslar().stream()
                        .map(d -> toDarsDTO(d, k.taqsimlash(), k.oquvYili(), kursNomi))
                        .collect(Collectors.toList()))
                .kursantlar(kursantJurnallar)
                .build();
    }

    // Fan nomi, o'qituvchi FIO va batafsil baholar — faqat bitta kursant uchun, engil
    // (Reyting daftarchasi rsem dan, Baholash hisobotlari esa qolgan maydonlardan foydalanadi)
    public record StudentFanNatijasi(String fanNomi, String oqituvchiFio,
                                     Double rkb1, Double rkb2,
                                     Double ronSem, Integer ryn, Integer rynEffektiv,
                                     Double rsem) {}

    // Reyting daftarchasi kabi joylar uchun: butun guruhni yuklamasdan,
    // faqat BITTA kursant uchun R(SEM) ni hisoblaydi (N+1 oldini olish uchun)
    public StudentFanNatijasi getStudentNatija(UUID oqituvchiFanTaqsimlashId,
                                               DarsTuri darsTuri,
                                               Semestr semestr,
                                               UUID oquvYiliId,
                                               UUID studentId) {

        JurnalKontekst k = tayyorlaKontekst(oqituvchiFanTaqsimlashId, darsTuri, semestr, oquvYiliId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Kursant topilmadi: " + studentId));

        ElektronJurnalResponseDTO.KursantJurnalDTO natija = buildKursantJurnal(
                student, k.darslar(), k.oraliq1lar(), k.oraliq2lar(), k.yilOraliqlari(),
                k.yakuniylar(), oqituvchiFanTaqsimlashId,
                k.oquvYiliBoshlanish(), k.mtTaqsimlashCandidateIds(), semestr, oquvYiliId);

        Integer rynEffektiv = natija.getRynQaytaTopshirishBaho() != null
                ? natija.getRynQaytaTopshirishBaho() : natija.getRyn();

        return new StudentFanNatijasi(
                k.taqsimlash().getFanTaqsimlash().getFan().getFanNomi(),
                k.taqsimlash().getOqituvchi().getFio(),
                natija.getRkb1(),
                natija.getRkb2(),
                natija.getRonSem(),
                natija.getRyn(),
                rynEffektiv,
                natija.getRsem());
    }

    // Yangi dars qo'shish (sana tanlanganda)
    @Transactional
    public DarsJurnaliResponseDTO darsQoshish(UUID oqituvchiFanTaqsimlashId,
                                              DarsTuri darsTuri, Semestr semestr, LocalDate sana) {

        OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi"));

        OquvYili faolYil = oquvYiliRepository.findByFaolTrue()
                .orElseThrow(() -> new NotFoundException("Faol o'quv yili topilmadi"));

        darsJurnaliRepository.findByOqituvchiFanTaqsimlashIdAndDarsTuriAndSana(
                        oqituvchiFanTaqsimlashId, darsTuri, sana)
                .ifPresent(d -> {
                    throw new ConflictException("Bu sana uchun dars allaqachon mavjud: " + sana);
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

        Group birinchiGuruh = (taqsimlash.getGuruhlar() != null && !taqsimlash.getGuruhlar().isEmpty())
                ? taqsimlash.getGuruhlar().get(0) : null;
        String kursNomi = effektivKursRaqami(birinchiGuruh, taqsimlash.getKurs(), faolYil) + "-kurs";

        return toDarsDTO(darsJurnali, taqsimlash, faolYil, kursNomi);
    }

    // Dars sanasini o'zgartirish (cheklovsiz — istalgan vaqtda)
    @Transactional
    public DarsJurnaliResponseDTO darsSanasiniOzgartirish(UUID darsJurnaliId, LocalDate yangiSana) {
        DarsJurnali darsJurnali = darsJurnaliRepository.findById(darsJurnaliId)
                .orElseThrow(() -> new NotFoundException("Dars topilmadi: " + darsJurnaliId));

        oquvYiliService.tahririshniTekshir(darsJurnali.getOquvYili().getId());

        darsJurnaliRepository.findByOqituvchiFanTaqsimlashIdAndDarsTuriAndSana(
                        darsJurnali.getOqituvchiFanTaqsimlash().getId(),
                        darsJurnali.getDarsTuri(), yangiSana)
                .filter(d -> !d.getId().equals(darsJurnaliId))
                .ifPresent(d -> {
                    throw new ConflictException("Bu sana uchun dars allaqachon mavjud: " + yangiSana);
                });

        darsJurnali.setSana(yangiSana);
        darsJurnali = darsJurnaliRepository.save(darsJurnali);

        OqituvchiFanTaqsimlash taqsimlash = darsJurnali.getOqituvchiFanTaqsimlash();
        Group birinchiGuruh = (taqsimlash.getGuruhlar() != null && !taqsimlash.getGuruhlar().isEmpty())
                ? taqsimlash.getGuruhlar().get(0) : null;
        String kursNomi = effektivKursRaqami(birinchiGuruh, taqsimlash.getKurs(), darsJurnali.getOquvYili()) + "-kurs";

        return toDarsDTO(darsJurnali, taqsimlash, darsJurnali.getOquvYili(), kursNomi);
    }

    // Darsni o'chirish (cheklovsiz — istalgan vaqtda), bog'liq davomatlar bilan birga
    @Transactional
    public void darsniOchirish(UUID darsJurnaliId) {
        DarsJurnali darsJurnali = darsJurnaliRepository.findById(darsJurnaliId)
                .orElseThrow(() -> new NotFoundException("Dars topilmadi: " + darsJurnaliId));

        oquvYiliService.tahririshniTekshir(darsJurnali.getOquvYili().getId());

        long topshiriqlarSoni = mustaqilTalimTopshiriqRepository
                .findByDarsJurnaliIdOrderByYaratilganVaqtAsc(darsJurnaliId).size();
        if (topshiriqlarSoni > 0) {
            throw new ConflictException(
                    "Bu mavzuga tegishli " + topshiriqlarSoni + " ta topshiriq mavjud! "
                            + "Avval o'sha topshiriq(lar)ni o'chiring, keyin mavzuni o'chirishingiz mumkin.");
        }

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
                .orElseThrow(() -> new NotFoundException("Davomat topilmadi: " + davomatId));

        oquvYiliService.tahririshniTekshir(davomat.getDarsJurnali().getOquvYili().getId());

        // Qayta topshirish bosqichimi: kursant avval qatnashmagan (N/K/S/Y)
        // YOKI darsni o'zlashtirmasdan eng past baho (2) olgan bo'lsa
        boolean qaytaTopshirishBosqichi = davomat.getHolat() != null
                || (davomat.getBaho() != null && davomat.getBaho() == 2);

        if (qaytaTopshirishBosqichi) {
            // Qayta topshirish har doim ruxsat etiladi - aynan shu orqali blok avtomatik ochiladi,
            // shuning uchun bloklanganMi tekshiruvi bu yerda qo'llanilmaydi
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

            // Qayta topshirgach, shu kursantning barcha bloklangan yozuvlari avtomatik ochiladi
            List<AmaliyDavomat> bloklanganlar = amaliyDavomatRepository
                    .findByStudentIdAndBloklanganMiTrue(davomat.getStudent().getId());
            for (AmaliyDavomat bloklangan : bloklanganlar) {
                bloklangan.setBloklanganMi(false);
                bloklangan.setBloklashSanasi(null);
                amaliyDavomatRepository.save(bloklangan);
            }
        } else {
            if (davomat.getBloklanganMi()) {
                throw new RuntimeException("Kursant bloklangan! Avval qayta topshirishi kerak.");
            }
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
    public void oraliqNazoratYangilash(UUID oqituvchiFanTaqsimlashId,
                                       UUID studentId, UUID oquvYiliId,
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

        oquvYiliService.tahririshniTekshir(oquvYiliId);

        OqituvchiFanTaqsimlash egaTaqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi"));
        if (!Boolean.TRUE.equals(egaTaqsimlash.getOraliqNazoratRuxsat())) {
            throw new RuntimeException(
                    "Oraliq nazorat kiritishga hali ruxsat berilmagan! Fakultet boshlig'iga murojaat qiling.");
        }

        OraliqNazorat nazorat = oraliqNazoratRepository
                .findByOqituvchiFanTaqsimlashIdAndStudentIdAndOquvYiliIdAndSemestrAndOraliqRaqami(
                        oqituvchiFanTaqsimlashId, studentId, oquvYiliId, semestr, oraliqRaqami)
                .orElse(null);

        if (nazorat == null) {
            OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                    .findById(oqituvchiFanTaqsimlashId)
                    .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi"));
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Kursant topilmadi"));
            OquvYili oquvYili = oquvYiliRepository.findById(oquvYiliId)
                    .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi"));

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
            // Agar birinchi baho allaqachon 2 bo'lsa - bu qayta topshirish bosqichi
            if (nazorat.getRonBaho() != null && nazorat.getRonBaho() == 2) {
                nazorat.setQaytaTopshirishBaho(baho);
            } else {
                nazorat.setKesimSanasi(kesimSanasi);
                nazorat.setRonBaho(baho);
            }
        }

        oraliqNazoratRepository.save(nazorat);
    }

    // Yakuniy nazorat bahosini kiritish/yangilash
    @Transactional
    public void yakuniyNazoratYangilash(UUID oqituvchiFanTaqsimlashId,
                                        UUID studentId, UUID oquvYiliId,
                                        LocalDate sana, Integer baho) {
        if (baho < 2 || baho > 5) {
            throw new RuntimeException("Baho 2, 3, 4 yoki 5 bo'lishi kerak!");
        }
        if (sana == null) {
            throw new RuntimeException("Imtihon sanasi kiritilishi shart!");
        }

        oquvYiliService.tahririshniTekshir(oquvYiliId);

        OqituvchiFanTaqsimlash egaTaqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi"));
        if (!Boolean.TRUE.equals(egaTaqsimlash.getYakuniyNazoratRuxsat())) {
            throw new RuntimeException(
                    "Yakuniy nazorat kiritishga hali ruxsat berilmagan! Fakultet boshlig'iga murojaat qiling.");
        }

        YakuniyNazorat nazorat = yakuniyNazoratRepository
                .findByOqituvchiFanTaqsimlashIdAndStudentIdAndOquvYiliId(
                        oqituvchiFanTaqsimlashId, studentId, oquvYiliId)
                .orElse(null);

        if (nazorat == null) {
            OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                    .findById(oqituvchiFanTaqsimlashId)
                    .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi"));
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Kursant topilmadi"));
            OquvYili oquvYili = oquvYiliRepository.findById(oquvYiliId)
                    .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi"));

            nazorat = YakuniyNazorat.builder()
                    .oqituvchiFanTaqsimlash(taqsimlash)
                    .student(student)
                    .oquvYili(oquvYili)
                    .yakunlanishSanasi(sana)
                    .ynBaho(baho)
                    .build();
        } else {
            // Agar birinchi baho allaqachon 2 bo'lsa - bu qayta topshirish bosqichi
            if (nazorat.getYnBaho() != null && nazorat.getYnBaho() == 2) {
                nazorat.setQaytaTopshirishBaho(baho);
            } else {
                nazorat.setYakunlanishSanasi(sana);
                nazorat.setYnBaho(baho);
            }
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
    // Shu davrda qayta topshirilmagan 2 baho bor-yo'qligini alohida tekshiradi
    // (R(KB) va shu bilan birga R(ON)ni ham to'liq bloklash uchun ishlatiladi)
    private boolean qaytaTopshirilmaganIkkiBormi(UUID studentId, UUID taqsimlashId,
                                                 LocalDate boshlanish, LocalDate tugash) {
        if (boshlanish == null || tugash == null || boshlanish.isAfter(tugash)) return false;
        List<AmaliyDavomat> baholangan = amaliyDavomatRepository
                .findBaholangan(studentId, taqsimlashId, boshlanish, tugash);
        return baholangan.stream()
                .anyMatch(d -> d.getBaho() != null && d.getBaho() == 2 && d.getQaytaTopshirishBaho() == null);
    }

    private Double hisoblaRkb(UUID studentId, UUID taqsimlashId,
                              LocalDate boshlanish, LocalDate tugash) {
        if (boshlanish == null || tugash == null || boshlanish.isAfter(tugash)) return null;
        List<AmaliyDavomat> baholangan = amaliyDavomatRepository
                .findBaholangan(studentId, taqsimlashId, boshlanish, tugash);
        if (baholangan.isEmpty()) return null;

        // Agar biror darsda 2 baho olib, hali qayta topshirmagan bo'lsa - R(KB) hisoblanmaydi
        // (qayta topshirilgach avtomatik hisoblanadi)
        boolean qaytaTopshirilmaganIkkiBor = baholangan.stream()
                .anyMatch(d -> d.getBaho() != null && d.getBaho() == 2 && d.getQaytaTopshirishBaho() == null);
        if (qaytaTopshirilmaganIkkiBor) return null;

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
            UUID taqsimlashId,
            LocalDate oquvYiliBoshlanish,
            List<UUID> mtTaqsimlashCandidateIds,
            Semestr semestr,
            UUID oquvYiliId) {

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

        // ===== 1-ORALIQ va 2-ORALIQ uchun kesim sanalarini avval aniqlaymiz =====
        OraliqNazorat oraliq1 = oraliq1lar.stream()
                .filter(o -> o.getStudent().getId().equals(student.getId()))
                .findFirst().orElse(null);
        OraliqNazorat oraliq2 = oraliq2lar.stream()
                .filter(o -> o.getStudent().getId().equals(student.getId()))
                .findFirst().orElse(null);

        LocalDate kesim1Sanasi = oraliq1 != null ? oraliq1.getKesimSanasi() : null;
        LocalDate kesim2Sanasi = oraliq2 != null ? oraliq2.getKesimSanasi() : null;
        Integer ron1 = oraliq1 != null ? oraliq1.getRonBaho() : null;
        Integer ron2 = oraliq2 != null ? oraliq2.getRonBaho() : null;
        Integer ron1QaytaTopshirishBaho = oraliq1 != null ? oraliq1.getQaytaTopshirishBaho() : null;
        Integer ron2QaytaTopshirishBaho = oraliq2 != null ? oraliq2.getQaytaTopshirishBaho() : null;
        Integer ron1Effektiv = ron1QaytaTopshirishBaho != null ? ron1QaytaTopshirishBaho : ron1;
        Integer ron2Effektiv = ron2QaytaTopshirishBaho != null ? ron2QaytaTopshirishBaho : ron2;
        // Agar birinchi baho 2 bo'lib, hali qayta topshirilmagan bo'lsa - R(1ON)/R(2ON) bloklanadi
        boolean ron1Bloklangan = ron1 != null && ron1 == 2 && ron1QaytaTopshirishBaho == null;
        boolean ron2Bloklangan = ron2 != null && ron2 == 2 && ron2QaytaTopshirishBaho == null;

        // R(MT-1)/R(MT-2) - Mustaqil ta'lim jurnalidan real vaqtda olinadi
        // (bir xil kesim sanalari qayta ishlatiladi, alohida kesim sanasi kiritilmaydi)
        Double[] rmtlar = mustaqilTalimJurnalService.hisoblaRmt1Rmt2(
                mtTaqsimlashCandidateIds, student.getId(), semestr, oquvYiliId, kesim1Sanasi, kesim2Sanasi);
        Double rmt1 = rmtlar[0];
        Double rmt2 = rmtlar[1];

        // ===== 1-ORALIQ =====
        Double rkb1;
        Double r1on;

        // Kesim sanasi kiritilgan bo'lsa - o'shanigacha; kiritilmagan bo'lsa - bugungi kungacha
        // (R(KB) har doim, oraliq nazorat kutilmasdan, avtomatik hisoblanadi)
        LocalDate effektivKesim1 = kesim1Sanasi != null ? kesim1Sanasi : CHEKSIZ_KELAJAK;
        LocalDate boshlanish1 = oldingiKesimSanasi(mendagiYilOraliqlari, effektivKesim1, oquvYiliBoshlanish);
        boolean bloklangan1 = qaytaTopshirilmaganIkkiBormi(student.getId(), taqsimlashId, boshlanish1, effektivKesim1);
        rkb1 = hisoblaRkb(student.getId(), taqsimlashId, boshlanish1, effektivKesim1);
        r1on = (bloklangan1 || ron1Bloklangan) ? null : hisoblaOraliqNatija(rkb1, ron1Effektiv, rmt1);

        // ===== 2-ORALIQ =====
        Double rkb2;
        Double r2on;

        if (kesim1Sanasi == null) {
            // 1-oraliq hali "qulflanmagan" (kesim sanasi kiritilmagan) - demak
            // 2-oraliq davri hali boshlanmagan, R(KB2) bo'sh qoladi
            rkb2 = null;
            r2on = null;
        } else {
            LocalDate effektivKesim2 = kesim2Sanasi != null ? kesim2Sanasi : CHEKSIZ_KELAJAK;
            // 2-oraliq har doim 1-oraliq tugagan joydan (kesim1Sanasi) boshlanadi
            boolean bloklangan2 = qaytaTopshirilmaganIkkiBormi(student.getId(), taqsimlashId, kesim1Sanasi, effektivKesim2);
            rkb2 = hisoblaRkb(student.getId(), taqsimlashId, kesim1Sanasi, effektivKesim2);
            r2on = (bloklangan2 || ron2Bloklangan) ? null : hisoblaOraliqNatija(rkb2, ron2Effektiv, rmt2);
        }

        // R(ON.SEM) = (R(1ON)+R(2ON))/2
        Double ronSem = null;
        if (r1on != null && r2on != null) {
            ronSem = yaxlaBaho((r1on + r2on) / 2);
        }

        // R(YN) — yakuniy nazorat bahosi (har doim ko'rsatiladi, "2/4" formatida bo'lishi uchun
        // asosiy baho va qayta topshirish bahosi alohida-alohida chiqariladi)
        YakuniyNazorat yakuniyYozuv = yakuniylar.stream()
                .filter(y -> y.getStudent().getId().equals(student.getId()))
                .findFirst()
                .orElse(null);
        Integer ryn = yakuniyYozuv != null ? yakuniyYozuv.getYnBaho() : null;
        Integer rynQaytaTopshirishBaho = yakuniyYozuv != null ? yakuniyYozuv.getQaytaTopshirishBaho() : null;

        // R(SEM) hisoblashda samarali baho: qayta topshirilgan bo'lsa - o'sha, aks holda asosiy baho
        Integer rynEffektiv = rynQaytaTopshirishBaho != null ? rynQaytaTopshirishBaho : ryn;
        // Agar birinchi baho 2 bo'lib, hali qayta topshirilmagan bo'lsa - R(SEM) bloklanadi
        boolean ynBloklangan = ryn != null && ryn == 2 && rynQaytaTopshirishBaho == null;

        // R(SEM) = (R(ON.SEM)+R(YN))/2
        Double rsem = null;
        if (!ynBloklangan && ronSem != null && rynEffektiv != null) {
            rsem = yaxlaBaho((ronSem + rynEffektiv) / 2);
        }

        return ElektronJurnalResponseDTO.KursantJurnalDTO.builder()
                .studentId(student.getId())
                .studentFio(student.getFio())
                .davomatlar(davomatlar)
                .rkb1(rkb1)
                .ron1(ron1)
                .ron1QaytaTopshirishBaho(ron1QaytaTopshirishBaho)
                .rmt1(rmt1)
                .r1on(r1on)
                .kesim1Sanasi(kesim1Sanasi)
                .rkb2(rkb2)
                .ron2(ron2)
                .ron2QaytaTopshirishBaho(ron2QaytaTopshirishBaho)
                .rmt2(rmt2)
                .r2on(r2on)
                .kesim2Sanasi(kesim2Sanasi)
                .ronSem(ronSem)
                .ryn(ryn)
                .rynQaytaTopshirishBaho(rynQaytaTopshirishBaho)
                .rsem(rsem)
                .build();
    }

    // R(1ON) yoki R(2ON) = R(KB), R(ON), R(MT) o'rtachasi, yaxlitlangan
    // R(1ON)/R(2ON) = (R(KB)+R(ON)+R(MT))/3 - lekin FAQAT barcha uchtasi mavjud bo'lsa.
    // R(ON)=2 holatidagi bloklash chaqiruvchi tomonda (ron1Bloklangan/ron2Bloklangan) hal qilinadi -
    // bu yerga faqat samarali (effektiv, ya'ni qayta topshirilgan bo'lsa o'shani) qiymat keladi
    private Double hisoblaOraliqNatija(Double rkb, Integer ron, Double rmt) {
        if (rkb == null || ron == null || rmt == null) return null;
        return yaxlaBaho((rkb + ron + rmt) / 3.0);
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

        // Kurs ishida davomat/holat tushunchasi yo'q - faqat baho qo'yiladi,
        // shuning uchun naryad/kasallik asosida avtomatik N/K belgilash o'tkazib yuboriladi
        boolean davomatKerak = darsJurnali.getDarsTuri() != DarsTuri.KURS_ISHI;

        for (Student student : kursantlar) {
            DavomatHolati holat = null;
            boolean bloklangan = false;

            if (davomatKerak) {
                // N: sutkalik naryadda bormi?
                if (sutkalikNaryadRepository.existsByStudentIdAndQabulQilishSanasi(
                        student.getId(), sana)) {
                    holat = DavomatHolati.N;
                }
                // K: kasallar ro'yxatida shu sanada bormi?
                else if (kasalRepository.existsActiveKasal(student.getId(), sana)) {
                    holat = DavomatHolati.K;
                }

                // Blok tekshiruvi (faqat shu fan/taqsimlash doirasida)
                bloklangan = bloklashniTekshir(student.getId(), taqsimlash.getId(), sana);
            }

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

    // Blok tekshiruvi: 7 kundan oshgan va qayta topshirilmagan (FAQAT shu fan/taqsimlash doirasida)
    private boolean bloklashniTekshir(UUID studentId, UUID taqsimlashId, LocalDate sana) {
        LocalDate yettaKunOldin = sana.minusDays(7);
        List<AmaliyDavomat> bloklashKeraklar = amaliyDavomatRepository
                .findBloklashKeraklarTaqsimlashBoyicha(studentId, taqsimlashId, yettaKunOldin);
        return !bloklashKeraklar.isEmpty();
    }

    private DarsJurnaliResponseDTO toDarsDTO(DarsJurnali entity,
                                             OqituvchiFanTaqsimlash taqsimlash,
                                             OquvYili oquvYili,
                                             String kursNomi) {
        String guruhNomi = taqsimlash.getGuruhlar() != null ?
                taqsimlash.getGuruhlar().stream()
                        .map(Group::getGuruhNomi)
                        .collect(Collectors.joining(", ")) : null;

        return DarsJurnaliResponseDTO.builder()
                .id(entity.getId())
                .oqituvchiFanTaqsimlashId(taqsimlash.getId())
                .fanNomi(taqsimlash.getFanTaqsimlash().getFan().getFanNomi())
                .oqituvchiFio(taqsimlash.getOqituvchi().getFio())
                .kursNomi(kursNomi)
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