package com.edu.talim.service;

import com.edu.talim.dto.DarsJurnaliResponseDTO;
import com.edu.talim.dto.DavomatResponseDTO;
import com.edu.talim.dto.MavzuDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DarsJurnaliService {

    private final DarsJurnaliRepository darsJurnaliRepository;
    private final DavomatRepository davomatRepository;
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

    // O'qituvchiga tegishli darslar ro'yxati (semestr bo'yicha filtrlangan)
    public List<DarsJurnaliResponseDTO> getAll(Long oqituvchiFanTaqsimlashId,
                                               DarsTuri darsTuri, Semestr semestr, Long oquvYiliId) {
        return darsJurnaliRepository
                .findByOqituvchiFanTaqsimlashIdAndDarsTuriAndOquvYiliIdAndSemestr(
                        oqituvchiFanTaqsimlashId, darsTuri, oquvYiliId, semestr)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Bitta dars
    public DarsJurnaliResponseDTO getById(Long id) {
        return toDTO(findById(id));
    }

    // Yangi dars qo'shish (sana va semestr tanlanganda)
    @Transactional
    public DarsJurnaliResponseDTO create(Long oqituvchiFanTaqsimlashId,
                                         DarsTuri darsTuri, Semestr semestr, LocalDate sana) {

        OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new RuntimeException("Fan taqsimlash topilmadi"));

        // Faol o'quv yilini avtomatik olish
        OquvYili faolYil = oquvYiliRepository.findByFaolTrue()
                .orElseThrow(() -> new RuntimeException("Faol o'quv yili topilmadi"));

        // Shu sana uchun dars allaqachon bormi
        darsJurnaliRepository.findByOqituvchiFanTaqsimlashIdAndDarsTuriAndSana(
                        oqituvchiFanTaqsimlashId, darsTuri, sana)
                .ifPresent(d -> {
                    throw new RuntimeException("Bu sana uchun dars allaqachon mavjud: " + sana);
                });

        // Avval davomatlarsiz saqlaymiz
        DarsJurnali darsJurnali = DarsJurnali.builder()
                .oqituvchiFanTaqsimlash(taqsimlash)
                .oquvYili(faolYil)
                .darsTuri(darsTuri)
                .semestr(semestr)
                .sana(sana)
                .soat(2)
                .build();

        darsJurnali = darsJurnaliRepository.save(darsJurnali);

        // Keyin davomatlarni alohida saqlaymiz
        yaratDavomatlar(darsJurnali, taqsimlash, sana);

        // Hibernate cache ni tozalab, bazadan qayta yuklaymiz
        darsJurnaliRepository.flush();
        entityManager.refresh(darsJurnali);

        return toDTO(darsJurnali);
    }

    // Mavzu nomi va soatni yangilash
    @Transactional
    public DarsJurnaliResponseDTO update(Long id, String mavzuNomi, Integer soat) {
        DarsJurnali darsJurnali = findById(id);

        if (mavzuNomi != null) darsJurnali.setMavzuNomi(mavzuNomi);
        if (soat != null) darsJurnali.setSoat(soat);

        return toDTO(darsJurnaliRepository.save(darsJurnali));
    }

    // Dars sanasini o'zgartirish (cheklovsiz — istalgan vaqtda)
    @Transactional
    public DarsJurnaliResponseDTO darsSanasiniOzgartirish(Long id, LocalDate yangiSana) {
        DarsJurnali darsJurnali = findById(id);

        darsJurnaliRepository.findByOqituvchiFanTaqsimlashIdAndDarsTuriAndSana(
                        darsJurnali.getOqituvchiFanTaqsimlash().getId(),
                        darsJurnali.getDarsTuri(), yangiSana)
                .filter(d -> !d.getId().equals(id))
                .ifPresent(d -> {
                    throw new RuntimeException("Bu sana uchun dars allaqachon mavjud: " + yangiSana);
                });

        darsJurnali.setSana(yangiSana);
        return toDTO(darsJurnaliRepository.save(darsJurnali));
    }

    // Darsni o'chirish (cheklovsiz — istalgan vaqtda).
    // Davomatlar avtomatik o'chadi (orphanRemoval=true, cascade=ALL DarsJurnali entity'sida)
    @Transactional
    public void darsniOchirish(Long id) {
        DarsJurnali darsJurnali = findById(id);
        darsJurnaliRepository.delete(darsJurnali);
    }

    // Faqat mashg'ulot mavzulari ro'yxati (yengil, boshqa joylarda qayta ishlatish uchun)
    public List<MavzuDTO> getMavzular(Long oqituvchiFanTaqsimlashId, DarsTuri darsTuri,
                                      Semestr semestr, Long oquvYiliId) {
        return darsJurnaliRepository
                .findByOqituvchiFanTaqsimlashIdAndDarsTuriAndOquvYiliIdAndSemestr(
                        oqituvchiFanTaqsimlashId, darsTuri, oquvYiliId, semestr)
                .stream()
                .sorted((a, b) -> a.getSana().compareTo(b.getSana()))
                .map(d -> MavzuDTO.builder()
                        .darsJurnaliId(d.getId())
                        .sana(d.getSana())
                        .mavzuNomi(d.getMavzuNomi())
                        .build())
                .collect(Collectors.toList());
    }

    // Topshiriq fayl yuklash
    @Transactional
    public DarsJurnaliResponseDTO topshiriqYuklash(Long id, MultipartFile fayl) throws IOException {
        DarsJurnali darsJurnali = findById(id);

        // Eski faylni o'chirish
        if (darsJurnali.getTopshiriqFaylYoli() != null) {
            Files.deleteIfExists(Paths.get(uploadDir, darsJurnali.getTopshiriqFaylYoli()));
        }

        // Yangi faylni saqlash
        String papka = uploadDir + "/jurnal-topshiriq";
        Files.createDirectories(Paths.get(papka));
        String kengaytma = getFileExtension(fayl.getOriginalFilename());
        String yangiNom = UUID.randomUUID() + "." + kengaytma;
        Files.copy(fayl.getInputStream(), Paths.get(papka, yangiNom),
                StandardCopyOption.REPLACE_EXISTING);

        darsJurnali.setTopshiriqFaylNomi(fayl.getOriginalFilename());
        darsJurnali.setTopshiriqFaylYoli("jurnal-topshiriq/" + yangiNom);

        return toDTO(darsJurnaliRepository.save(darsJurnali));
    }

    // Davomat holatini yangilash (S, Y, N_T, K_T, S_T, Y_T)
    @Transactional
    public DavomatResponseDTO davomatYangilash(Long davomatId, DavomatHolati holat) {
        Davomat davomat = davomatRepository.findById(davomatId)
                .orElseThrow(() -> new RuntimeException("Davomat topilmadi: " + davomatId));

        // Bloklangan kursantni tekshirish
        if (davomat.getBloklanganMi()) {
            throw new RuntimeException("Kursant bloklangan! Dekanat ruxsati kerak.");
        }

        DavomatHolati joriyHolat = davomat.getHolat();

        // Agar kursant avval N/K/S/Y bilan belgilangan bo'lsa — endi faqat mos "T"
        // (qayta topshirish) qiymatini qo'yish mumkin: N->N_T, K->K_T, S->S_T, Y->Y_T
        if (joriyHolat != null && !joriyHolat.name().endsWith("_T")) {
            DavomatHolati kutilganQaytaTopshirish = DavomatHolati.valueOf(joriyHolat.name() + "_T");
            if (holat == null || holat != kutilganQaytaTopshirish) {
                throw new RuntimeException(
                        "Bu amalni amalga oshirolmaysiz! Qayta topshirish faqat \""
                                + kutilganQaytaTopshirish + "\" (T) belgisi orqali amalga oshiriladi.");
            }
        }

        davomat.setHolat(holat);
        return toDavomatDTO(davomatRepository.save(davomat));
    }

    // ====================== Yordamchi metodlar ======================

    // Guruhidagi barcha kursantlar uchun davomat yaratish
    private void yaratDavomatlar(DarsJurnali darsJurnali,
                                 OqituvchiFanTaqsimlash taqsimlash,
                                 LocalDate sana) {
        List<Student> kursantlar = new ArrayList<>();
        if (taqsimlash.getGuruhlar() != null) {
            for (var guruh : taqsimlash.getGuruhlar()) {
                // Fio bo'yicha alifbo tartibida saralangan kursantlar
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

            // Blok tekshiruvi: 7 kundan oshgan va qayta topshirilmagan dars bormi?
            boolean bloklangan = bloklashniTekshir(student.getId(), sana);

            Davomat davomat = Davomat.builder()
                    .darsJurnali(darsJurnali)
                    .student(student)
                    .holat(holat)
                    .bloklanganMi(bloklangan)
                    .bloklashSanasi(bloklangan ? sana : null)
                    .build();

            davomatRepository.save(davomat);
        }
    }

    // Bloklash tekshiruvi: 7 kundan oshgan va qayta topshirilmagan dars bormi?
    private boolean bloklashniTekshir(Long studentId, LocalDate sana) {
        LocalDate yettaKunOldin = sana.minusDays(7);
        List<Davomat> bloklashKeraklar = davomatRepository
                .findBloklashKeraklar(studentId, yettaKunOldin);
        return !bloklashKeraklar.isEmpty();
    }

    private DarsJurnali findById(Long id) {
        return darsJurnaliRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dars jurnali topilmadi: " + id));
    }

    private String getFileExtension(String faylNomi) {
        if (faylNomi == null || !faylNomi.contains(".")) return "pdf";
        return faylNomi.substring(faylNomi.lastIndexOf(".") + 1).toLowerCase();
    }

    private DarsJurnaliResponseDTO toDTO(DarsJurnali entity) {
        // Davomatlarni fio bo'yicha alifbo tartibida saralash
        List<DavomatResponseDTO> davomatDTOs = entity.getDavomatlar() == null ? List.of() :
                entity.getDavomatlar().stream()
                        .sorted((a, b) -> a.getStudent().getFio()
                                .compareTo(b.getStudent().getFio()))
                        .map(this::toDavomatDTO)
                        .collect(Collectors.toList());

        // Guruhlar nomini birlashtirish
        String guruhNomi = null;
        if (entity.getOqituvchiFanTaqsimlash().getGuruhlar() != null) {
            guruhNomi = entity.getOqituvchiFanTaqsimlash().getGuruhlar()
                    .stream()
                    .map(Group::getGuruhNomi)
                    .collect(Collectors.joining(", "));
        }

        return DarsJurnaliResponseDTO.builder()
                .id(entity.getId())
                .oqituvchiFanTaqsimlashId(entity.getOqituvchiFanTaqsimlash().getId())
                .fanNomi(entity.getOqituvchiFanTaqsimlash().getFanTaqsimlash().getFan().getFanNomi())
                .oqituvchiFio(entity.getOqituvchiFanTaqsimlash().getOqituvchi().getFio())
                .kursNomi(entity.getOqituvchiFanTaqsimlash().getKurs().getKursRaqami() + "-kurs")
                .guruhNomi(guruhNomi)
                .oquvYiliId(entity.getOquvYili().getId())
                .oquvYiliNomi(entity.getOquvYili().getNom())
                .darsTuri(entity.getDarsTuri())
                .semestr(entity.getSemestr())
                .sana(entity.getSana())
                .soat(entity.getSoat())
                .mavzuNomi(entity.getMavzuNomi())
                .topshiriqFaylNomi(entity.getTopshiriqFaylNomi())
                .topshiriqFaylUrl(entity.getTopshiriqFaylYoli() != null ?
                        baseUrl + "/uploads/" + entity.getTopshiriqFaylYoli() : null)
                .davomatlar(davomatDTOs)
                .build();
    }

    private DavomatResponseDTO toDavomatDTO(Davomat d) {
        return DavomatResponseDTO.builder()
                .id(d.getId())
                .studentId(d.getStudent().getId())
                .studentFio(d.getStudent().getFio())
                .holat(d.getHolat())
                .bloklanganMi(d.getBloklanganMi())
                .build();
    }
}