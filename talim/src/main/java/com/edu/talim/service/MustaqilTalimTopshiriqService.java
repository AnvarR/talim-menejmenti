package com.edu.talim.service;

import com.edu.talim.dto.*;
import com.edu.talim.entity.*;
import com.edu.talim.entity.enums.TopshiriqHolati;
import com.edu.talim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MustaqilTalimTopshiriqService {

    private final MustaqilTalimTopshiriqRepository topshiriqRepository;
    private final TopshiriqFaylRepository topshiriqFaylRepository;
    private final TopshiriqYuborishRepository topshiriqYuborishRepository;
    private final TopshiriqJavobRepository topshiriqJavobRepository;
    private final DarsJurnaliRepository darsJurnaliRepository;
    private final OqituvchiFanTaqsimlashRepository oqituvchiFanTaqsimlashRepository;
    private final StudentRepository studentRepository;
    private final OquvYiliService oquvYiliService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    // ====================== Topshiriq CRUD ======================

    // Bitta mavzuga (dars jurnali) tegishli barcha topshiriqlar ro'yxati
    public List<MustaqilTalimTopshiriqResponseDTO> getByMavzu(Long darsJurnaliId) {
        return topshiriqRepository.findByDarsJurnaliIdOrderByYaratilganVaqtAsc(darsJurnaliId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MustaqilTalimTopshiriqResponseDTO getById(Long id) {
        return toDTO(findById(id));
    }

    @Transactional
    public MustaqilTalimTopshiriqResponseDTO create(Long oqituvchiFanTaqsimlashId, Long darsJurnaliId,
                                                    String topshiriqTuri, String nomi, String izoh,
                                                    LocalDateTime boshlanishSanasi, LocalDateTime yakunlanishSanasi,
                                                    Integer urinishlarSoni,
                                                    List<MultipartFile> fayllar) throws IOException {

        OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new RuntimeException("Fan taqsimlash topilmadi"));

        // Himoya: Mustaqil ta'lim topshirig'i faqat MUSTAQIL_TALIM turidagi taqsimlashga yaratilishi mumkin
        if (taqsimlash.getDarsTuri() != com.edu.talim.entity.enums.DarsTuri.MUSTAQIL_TALIM) {
            throw new RuntimeException(
                    "Bu taqsimlash Mustaqil ta'lim turiga tegishli emas! (taqsimlashId=" + oqituvchiFanTaqsimlashId
                            + ", dars_turi=" + taqsimlash.getDarsTuri()
                            + "). Frontendda \"Mustaqil ta'lim\" bo'limi uchun to'g'ri taqsimlashId yuborilganini tekshiring.");
        }

        DarsJurnali mavzu = darsJurnaliRepository.findById(darsJurnaliId)
                .orElseThrow(() -> new RuntimeException("Mavzu topilmadi"));

        // Himoya: mavzu shu fanga (fanTaqsimlash) tegishli ekanini tekshirish - mavzular
        // dars turi/guruhdan qat'i nazar butun fan (masalan Ma'ruzada yaratilgan) doirasida umumiy
        Long mavzuFanTaqsimlashId = mavzu.getOqituvchiFanTaqsimlash().getFanTaqsimlash().getId();
        Long soralganFanTaqsimlashId = taqsimlash.getFanTaqsimlash().getId();
        if (!mavzuFanTaqsimlashId.equals(soralganFanTaqsimlashId)) {
            throw new RuntimeException(
                    "Bu mavzu boshqa fanga tegishli! (mavzu fanTaqsimlashId="
                            + mavzuFanTaqsimlashId
                            + ", so'ralgan fanTaqsimlashId=" + soralganFanTaqsimlashId + ")");
        }

        MustaqilTalimTopshiriq topshiriq = MustaqilTalimTopshiriq.builder()
                .oqituvchiFanTaqsimlash(taqsimlash)
                .darsJurnali(mavzu)
                .topshiriqTuri(topshiriqTuri)
                .nomi(nomi)
                .izoh(izoh)
                .boshlanishSanasi(boshlanishSanasi)
                .yakunlanishSanasi(yakunlanishSanasi)
                .urinishlarSoni(urinishlarSoni)
                .yuborilganMi(false)
                .build();

        topshiriq = topshiriqRepository.save(topshiriq);

        if (fayllar != null && !fayllar.isEmpty()) {
            saqlaFayllar(topshiriq, fayllar);
        }

        return toDTO(findById(topshiriq.getId()));
    }

    @Transactional
    public MustaqilTalimTopshiriqResponseDTO update(Long id, String topshiriqTuri, String nomi, String izoh,
                                                    LocalDateTime boshlanishSanasi, LocalDateTime yakunlanishSanasi,
                                                    Integer urinishlarSoni) {
        MustaqilTalimTopshiriq topshiriq = findById(id);

        if (topshiriqTuri != null) topshiriq.setTopshiriqTuri(topshiriqTuri);
        if (nomi != null) topshiriq.setNomi(nomi);
        if (izoh != null) topshiriq.setIzoh(izoh);
        if (boshlanishSanasi != null) topshiriq.setBoshlanishSanasi(boshlanishSanasi);
        if (yakunlanishSanasi != null) topshiriq.setYakunlanishSanasi(yakunlanishSanasi);
        if (urinishlarSoni != null) topshiriq.setUrinishlarSoni(urinishlarSoni);

        return toDTO(topshiriqRepository.save(topshiriq));
    }

    @Transactional
    public void delete(Long id) {
        MustaqilTalimTopshiriq topshiriq = findById(id);

        // Fayllarni diskdan o'chirish
        topshiriq.getFayllar().forEach(f -> faylniDiskdanOchirish(f.getFaylYoli()));

        topshiriqRepository.delete(topshiriq);
    }

    @Transactional
    public MustaqilTalimTopshiriqResponseDTO faylQoshish(Long id, List<MultipartFile> fayllar) throws IOException {
        MustaqilTalimTopshiriq topshiriq = findById(id);
        saqlaFayllar(topshiriq, fayllar);
        return toDTO(findById(id));
    }

    @Transactional
    public void faylOchirish(Long faylId) {
        TopshiriqFayl fayl = topshiriqFaylRepository.findById(faylId)
                .orElseThrow(() -> new RuntimeException("Fayl topilmadi"));
        faylniDiskdanOchirish(fayl.getFaylYoli());
        topshiriqFaylRepository.delete(fayl);
    }

    // ====================== Yuborish ======================

    // Tanlangan kursantlarga topshiriqni yuborish (har biriga alohida muddat bilan)
    @Transactional
    public MustaqilTalimTopshiriqResponseDTO yuborish(TopshiriqYuborishRequestDTO dto) {
        MustaqilTalimTopshiriq topshiriq = findById(dto.getTopshiriqId());

        for (TopshiriqYuborishRequestDTO.Item item : dto.getKursantlar()) {
            // Agar shu kursantga avval yuborilgan bo'lsa, qayta yuborilmaydi
            boolean allaqachonBor = !topshiriqYuborishRepository
                    .findByTopshiriqIdAndStudentId(topshiriq.getId(), item.getStudentId())
                    .isEmpty();
            if (allaqachonBor) continue;

            Student student = studentRepository.findById(item.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Kursant topilmadi: " + item.getStudentId()));

            TopshiriqYuborish yuborish = TopshiriqYuborish.builder()
                    .topshiriq(topshiriq)
                    .student(student)
                    .muddat(item.getMuddat())
                    .holati(TopshiriqHolati.BERILDI)
                    .build();

            topshiriqYuborishRepository.save(yuborish);
        }

        topshiriq.setYuborilganMi(true);
        topshiriqRepository.save(topshiriq);

        return toDTO(findById(topshiriq.getId()));
    }

    // ====================== Topshiriq holati (kursantlar bo'yicha umumiy ro'yxat) ======================

    public TopshiriqHolatiSahifaDTO getTopshiriqHolati(Long darsJurnaliId, String guruhNomi,
                                                       TopshiriqHolati holati, String topshiriqTuri) {

        DarsJurnali mavzu = darsJurnaliRepository.findById(darsJurnaliId)
                .orElseThrow(() -> new RuntimeException("Mavzu topilmadi"));

        List<TopshiriqYuborishHolatiDTO> qatorlar = topshiriqYuborishRepository
                .findByTopshiriq_DarsJurnaliId(darsJurnaliId)
                .stream()
                .filter(y -> guruhNomi == null ||
                        (y.getStudent().getGroup() != null &&
                                guruhNomi.equals(y.getStudent().getGroup().getGuruhNomi())))
                .filter(y -> holati == null || y.getHolati() == holati)
                .filter(y -> topshiriqTuri == null ||
                        topshiriqTuri.equalsIgnoreCase(y.getTopshiriq().getTopshiriqTuri()))
                .map(this::toHolatiDTO)
                .collect(Collectors.toList());

        TopshiriqHolatiSahifaDTO.MalumotDTO malumot = TopshiriqHolatiSahifaDTO.MalumotDTO.builder()
                .oquvYiliNomi(mavzu.getOquvYili().getNom())
                .semestr(mavzu.getSemestr())
                .fanNomi(mavzu.getOqituvchiFanTaqsimlash().getFanTaqsimlash().getFan().getFanNomi())
                .mashgulotNomi("Mustaqil ta'lim")
                .mavzuNomi(mavzu.getMavzuNomi())
                .build();

        return TopshiriqHolatiSahifaDTO.builder()
                .qatorlar(qatorlar)
                .malumot(malumot)
                .build();
    }

    // ====================== Javoblar va baholash ======================

    public List<TopshiriqJavobResponseDTO> getJavoblar(Long topshiriqYuborishId) {
        return topshiriqJavobRepository
                .findByTopshiriqYuborishIdOrderByBerilganSanaDesc(topshiriqYuborishId)
                .stream()
                .map(this::toJavobDTO)
                .collect(Collectors.toList());
    }

    // Kursant tomonidan javob berish (izoh + fayl)
    @Transactional
    public TopshiriqJavobResponseDTO javobBerish(Long topshiriqYuborishId, String izoh,
                                                 MultipartFile fayl) throws IOException {
        TopshiriqYuborish yuborish = topshiriqYuborishRepository.findById(topshiriqYuborishId)
                .orElseThrow(() -> new RuntimeException("Topshiriq topilmadi"));

        Integer urinishlarSoni = yuborish.getTopshiriq().getUrinishlarSoni();
        if (urinishlarSoni != null) {
            long borUrinish = topshiriqJavobRepository
                    .findByTopshiriqYuborishIdOrderByBerilganSanaDesc(topshiriqYuborishId).size();
            if (borUrinish >= urinishlarSoni) {
                throw new RuntimeException("Ruxsat etilgan urinishlar soni (" + urinishlarSoni + ") tugagan!");
            }
        }

        TopshiriqJavob javob = TopshiriqJavob.builder()
                .topshiriqYuborish(yuborish)
                .izoh(izoh)
                .berilganSana(LocalDateTime.now())
                .build();

        if (fayl != null && !fayl.isEmpty()) {
            String papka = uploadDir + "/topshiriq-javoblar";
            Files.createDirectories(Paths.get(papka));

            String kengaytma = getFileExtension(fayl.getOriginalFilename());
            String yangiNom = UUID.randomUUID() + "." + kengaytma;
            Path faylYoli = Paths.get(papka, yangiNom);
            Files.copy(fayl.getInputStream(), faylYoli, StandardCopyOption.REPLACE_EXISTING);

            javob.setFaylNomi(fayl.getOriginalFilename());
            javob.setFaylYoli("topshiriq-javoblar/" + yangiNom);
        }

        javob = topshiriqJavobRepository.save(javob);

        yuborish.setHolati(TopshiriqHolati.TOPSHIRDI);
        topshiriqYuborishRepository.save(yuborish);

        return toJavobDTO(javob);
    }

    // O'qituvchi tomonidan baholash
    @Transactional
    public TopshiriqJavobResponseDTO baholash(Long javobId, BaholashRequestDTO dto) {
        TopshiriqJavob javob = topshiriqJavobRepository.findById(javobId)
                .orElseThrow(() -> new RuntimeException("Javob topilmadi"));

        oquvYiliService.tahririshniTekshir(
                javob.getTopshiriqYuborish().getTopshiriq().getDarsJurnali().getOquvYili().getId());

        if (dto.getBaho() < 2 || dto.getBaho() > 5) {
            throw new RuntimeException("Baho 2, 3, 4 yoki 5 bo'lishi kerak!");
        }

        javob.setBaho(dto.getBaho());
        javob.setBaholashSharhi(dto.getBaholashSharhi());
        javob.setBaholanganSana(LocalDateTime.now());
        javob = topshiriqJavobRepository.save(javob);

        TopshiriqYuborish yuborish = javob.getTopshiriqYuborish();
        yuborish.setHolati(TopshiriqHolati.BAHOLANDI);
        topshiriqYuborishRepository.save(yuborish);

        return toJavobDTO(javob);
    }

    // O'qituvchi tomonidan baholamasdan qaytarish (kursant qayta topshirishi kerak)
    @Transactional
    public TopshiriqJavobResponseDTO qaytarish(Long javobId, String sabab) {
        TopshiriqJavob javob = topshiriqJavobRepository.findById(javobId)
                .orElseThrow(() -> new RuntimeException("Javob topilmadi"));

        javob.setQaytarilganMi(true);
        javob.setQaytarishSababi(sabab);
        javob = topshiriqJavobRepository.save(javob);

        TopshiriqYuborish yuborish = javob.getTopshiriqYuborish();
        yuborish.setHolati(TopshiriqHolati.QAYTARILDI);
        topshiriqYuborishRepository.save(yuborish);

        return toJavobDTO(javob);
    }

    // ====================== Kursant tarafi ======================

    // Kursantga tegishli barcha fan/mavzular ro'yxati, har biriga nechta topshiriq
    // yuborilganini ko'rsatadi (1-rasm)
    public List<KursantFanMavzuDTO> getFanlarVaMavzular(Long studentId) {
        List<TopshiriqYuborish> yuborishlar = topshiriqYuborishRepository.findByStudentId(studentId);

        // Mavzu (darsJurnali) bo'yicha guruhlash
        return yuborishlar.stream()
                .collect(Collectors.groupingBy(y -> y.getTopshiriq().getDarsJurnali()))
                .entrySet().stream()
                .map(entry -> {
                    DarsJurnali mavzu = entry.getKey();
                    long soni = entry.getValue().stream()
                            .map(y -> y.getTopshiriq().getId())
                            .distinct()
                            .count();
                    return KursantFanMavzuDTO.builder()
                            .darsJurnaliId(mavzu.getId())
                            .fanNomi(mavzu.getOqituvchiFanTaqsimlash().getFanTaqsimlash().getFan().getFanNomi())
                            .mavzuNomi(mavzu.getMavzuNomi())
                            .topshiriqlarSoni(soni)
                            .semestr(mavzu.getSemestr())
                            .oquvYiliNomi(mavzu.getOquvYili().getNom())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Kursantga shu mavzu bo'yicha yuborilgan barcha topshiriqlar (statusi bilan) (2-rasm)
    public List<KursantTopshiriqDTO> getMeningTopshiriqlarim(Long studentId, Long darsJurnaliId) {
        return topshiriqYuborishRepository
                .findByStudentIdAndTopshiriq_DarsJurnaliId(studentId, darsJurnaliId)
                .stream()
                .map(this::toKursantTopshiriqDTO)
                .collect(Collectors.toList());
    }

    private KursantTopshiriqDTO toKursantTopshiriqDTO(TopshiriqYuborish y) {
        List<TopshiriqJavob> javoblar = topshiriqJavobRepository
                .findByTopshiriqYuborishIdOrderByBerilganSanaDesc(y.getId());
        TopshiriqJavob oxirgiJavob = javoblar.isEmpty() ? null : javoblar.get(0);

        MustaqilTalimTopshiriq topshiriq = y.getTopshiriq();

        return KursantTopshiriqDTO.builder()
                .topshiriqYuborishId(y.getId())
                .topshiriqId(topshiriq.getId())
                .nomi(topshiriq.getNomi())
                .topshiriqTuri(topshiriq.getTopshiriqTuri())
                .fayllarSoni(topshiriq.getFayllar() != null ? topshiriq.getFayllar().size() : 0)
                .muddat(y.getMuddat())
                .holati(y.getHolati())
                .oxirgiBaho(oxirgiJavob != null ? oxirgiJavob.getBaho() : null)
                .baholashSharhi(oxirgiJavob != null ? oxirgiJavob.getBaholashSharhi() : null)
                .qaytarishSababi(oxirgiJavob != null ? oxirgiJavob.getQaytarishSababi() : null)
                .build();
    }

    // ====================== Yordamchi metodlar ======================

    private MustaqilTalimTopshiriq findById(Long id) {
        return topshiriqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topshiriq topilmadi: " + id));
    }

    private void saqlaFayllar(MustaqilTalimTopshiriq topshiriq, List<MultipartFile> fayllar) throws IOException {
        String papka = uploadDir + "/mustaqil-talim-topshiriqlar";
        Files.createDirectories(Paths.get(papka));

        for (MultipartFile fayl : fayllar) {
            String kengaytma = getFileExtension(fayl.getOriginalFilename());
            String yangiNom = UUID.randomUUID() + "." + kengaytma;
            Path faylYoli = Paths.get(papka, yangiNom);
            Files.copy(fayl.getInputStream(), faylYoli, StandardCopyOption.REPLACE_EXISTING);

            TopshiriqFayl entity = TopshiriqFayl.builder()
                    .topshiriq(topshiriq)
                    .faylNomi(fayl.getOriginalFilename())
                    .faylYoli("mustaqil-talim-topshiriqlar/" + yangiNom)
                    .build();

            topshiriqFaylRepository.save(entity);
        }
    }

    private void faylniDiskdanOchirish(String faylYoli) {
        try {
            Files.deleteIfExists(Paths.get(uploadDir, faylYoli));
        } catch (IOException e) {
            // davom etamiz
        }
    }

    private String getFileExtension(String faylNomi) {
        if (faylNomi == null || !faylNomi.contains(".")) {
            throw new RuntimeException("Fayl nomi noto'g'ri");
        }
        return faylNomi.substring(faylNomi.lastIndexOf(".") + 1).toLowerCase();
    }

    private MustaqilTalimTopshiriqResponseDTO toDTO(MustaqilTalimTopshiriq entity) {
        List<MustaqilTalimTopshiriqResponseDTO.TopshiriqFaylDTO> faylDTOs = entity.getFayllar() == null ?
                List.of() : entity.getFayllar().stream()
                .map(f -> MustaqilTalimTopshiriqResponseDTO.TopshiriqFaylDTO.builder()
                        .id(f.getId())
                        .faylNomi(f.getFaylNomi())
                        .faylUrl(baseUrl + "/uploads/" + f.getFaylYoli())
                        .build())
                .collect(Collectors.toList());

        long jami = topshiriqYuborishRepository.countByTopshiriqId(entity.getId());
        long javobBerganlar = topshiriqYuborishRepository
                .countByTopshiriqIdAndHolatiNot(entity.getId(), TopshiriqHolati.BERILDI);
        long baholanganlar = topshiriqYuborishRepository
                .countByTopshiriqIdAndHolati(entity.getId(), TopshiriqHolati.BAHOLANDI);

        return MustaqilTalimTopshiriqResponseDTO.builder()
                .id(entity.getId())
                .darsJurnaliId(entity.getDarsJurnali().getId())
                .mavzuNomi(entity.getDarsJurnali().getMavzuNomi())
                .topshiriqTuri(entity.getTopshiriqTuri())
                .nomi(entity.getNomi())
                .izoh(entity.getIzoh())
                .boshlanishSanasi(entity.getBoshlanishSanasi())
                .yakunlanishSanasi(entity.getYakunlanishSanasi())
                .urinishlarSoni(entity.getUrinishlarSoni())
                .yuborilganMi(entity.getYuborilganMi())
                .yaratilganVaqt(entity.getYaratilganVaqt())
                .fayllar(faylDTOs)
                .jamiYuborilgan(jami)
                .javobBerganlar(javobBerganlar)
                .baholanganlar(baholanganlar)
                .build();
    }

    private TopshiriqYuborishHolatiDTO toHolatiDTO(TopshiriqYuborish y) {
        List<TopshiriqJavob> javoblar = topshiriqJavobRepository
                .findByTopshiriqYuborishIdOrderByBerilganSanaDesc(y.getId());
        TopshiriqJavob oxirgiJavob = javoblar.isEmpty() ? null : javoblar.get(0);

        return TopshiriqYuborishHolatiDTO.builder()
                .topshiriqYuborishId(y.getId())
                .studentId(y.getStudent().getId())
                .studentFio(y.getStudent().getFio())
                .guruhNomi(y.getStudent().getGroup() != null ? y.getStudent().getGroup().getGuruhNomi() : null)
                .topshiriqTuri(y.getTopshiriq().getTopshiriqTuri())
                .topshiriqId(y.getTopshiriq().getId())
                .mavzuNomi(y.getTopshiriq().getDarsJurnali().getMavzuNomi())
                .holati(y.getHolati())
                .oxirgiBaho(oxirgiJavob != null ? oxirgiJavob.getBaho() : null)
                .muddat(y.getMuddat())
                .oxirgiHarakatSanasi(oxirgiJavob != null ? oxirgiJavob.getBerilganSana() : null)
                .build();
    }

    private TopshiriqJavobResponseDTO toJavobDTO(TopshiriqJavob j) {
        return TopshiriqJavobResponseDTO.builder()
                .id(j.getId())
                .izoh(j.getIzoh())
                .faylNomi(j.getFaylNomi())
                .faylUrl(j.getFaylYoli() != null ? baseUrl + "/uploads/" + j.getFaylYoli() : null)
                .berilganSana(j.getBerilganSana())
                .baho(j.getBaho())
                .baholashSharhi(j.getBaholashSharhi())
                .baholanganSana(j.getBaholanganSana())
                .qaytarilganMi(j.getQaytarilganMi())
                .qaytarishSababi(j.getQaytarishSababi())
                .build();
    }
}