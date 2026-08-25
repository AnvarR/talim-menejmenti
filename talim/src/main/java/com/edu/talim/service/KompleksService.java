package com.edu.talim.service;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.KompleksFaylDTO;
import com.edu.talim.dto.KompleksResponseDTO;
import com.edu.talim.entity.Kompleks;
import com.edu.talim.entity.KompleksFayl;
import com.edu.talim.entity.OquvYili;
import com.edu.talim.entity.OqituvchiFanTaqsimlash;
import com.edu.talim.entity.enums.MaterialKategoriyasi;
import com.edu.talim.repository.KompleksFaylRepository;
import com.edu.talim.repository.KompleksRepository;
import com.edu.talim.repository.OqituvchiFanTaqsimlashRepository;
import com.edu.talim.repository.OquvYiliRepository;
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
public class KompleksService {

    private final KompleksRepository kompleksRepository;
    private final KompleksFaylRepository kompleksFaylRepository;
    private final OqituvchiFanTaqsimlashRepository oqituvchiFanTaqsimlashRepository;
    private final OquvYiliRepository oquvYiliRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    private static final long VIDEO_MAX_SIZE = 50L * 1024 * 1024; // 50MB
    private static final long ODDIY_MAX_SIZE = 5L * 1024 * 1024;  // 5MB
    private static final List<String> VIDEO_KENGAYTMALAR =
            List.of("mp4", "avi", "mov", "mkv", "wmv");

    public List<KompleksResponseDTO> getAll() {
        return kompleksRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public KompleksResponseDTO getById(UUID id) {
        Kompleks kompleks = kompleksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kompleks topilmadi: " + id));
        return toDTO(kompleks);
    }

    @Transactional
    public KompleksResponseDTO create(UUID oqituvchiFanTaqsimlashId, String materialNomi,
                                      MaterialKategoriyasi kategoriya, List<MultipartFile> fayllar) throws IOException {

        OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository.findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi: " + oqituvchiFanTaqsimlashId));

        OquvYili faolYil = oquvYiliRepository.findByFaolTrue()
                .orElseThrow(() -> new NotFoundException("Faol o'quv yili topilmadi"));

        Kompleks kompleks = Kompleks.builder()
                .oqituvchiFanTaqsimlash(taqsimlash)
                .oquvYili(faolYil)
                .materialNomi(materialNomi)
                .materialKategoriyasi(kategoriya)
                .biriktirilganVaqt(LocalDateTime.now())
                .fayllar(new ArrayList<>())
                .build();

        kompleks = kompleksRepository.save(kompleks);

        List<KompleksFayl> faylEntitylar = saqlaFayllar(kompleks, fayllar);
        kompleks.setFayllar(faylEntitylar);

        return toDTO(kompleks);
    }

    @Transactional
    public KompleksResponseDTO update(UUID id, String materialNomi, MaterialKategoriyasi kategoriya,
                                      List<MultipartFile> yangiFayllar) throws IOException {

        Kompleks kompleks = kompleksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kompleks topilmadi: " + id));

        kompleks.setMaterialNomi(materialNomi);
        kompleks.setMaterialKategoriyasi(kategoriya);

        if (yangiFayllar != null && !yangiFayllar.isEmpty()) {
            List<KompleksFayl> qoshilganlar = saqlaFayllar(kompleks, yangiFayllar);
            kompleks.getFayllar().addAll(qoshilganlar);
        }

        return toDTO(kompleksRepository.save(kompleks));
    }

    @Transactional
    public void delete(UUID id) {
        Kompleks kompleks = kompleksRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Kompleks topilmadi: " + id));

        for (KompleksFayl fayl : kompleks.getFayllar()) {
            faylniDiskdanOchirish(fayl.getFaylYoli());
        }

        kompleksRepository.deleteById(id);
    }

    @Transactional
    public void faylniOchirish(UUID kompleksId, UUID faylId) {
        KompleksFayl fayl = kompleksFaylRepository.findById(faylId)
                .orElseThrow(() -> new NotFoundException("Fayl topilmadi: " + faylId));

        if (!fayl.getKompleks().getId().equals(kompleksId)) {
            throw new RuntimeException("Fayl bu komplektga tegishli emas");
        }

        faylniDiskdanOchirish(fayl.getFaylYoli());
        kompleksFaylRepository.deleteById(faylId);
    }

    // ====================== Yordamchi metodlar ======================

    private List<KompleksFayl> saqlaFayllar(Kompleks kompleks, List<MultipartFile> fayllar) throws IOException {
        List<KompleksFayl> natija = new ArrayList<>();

        String papka = uploadDir + "/komplekslar";
        Files.createDirectories(Paths.get(papka));

        for (MultipartFile fayl : fayllar) {
            String kengaytma = getFileExtension(fayl.getOriginalFilename());
            validateFaylHajmi(fayl, kengaytma);

            String yangiNom = UUID.randomUUID() + "." + kengaytma;
            Path faylYoli = Paths.get(papka, yangiNom);
            Files.copy(fayl.getInputStream(), faylYoli, StandardCopyOption.REPLACE_EXISTING);

            KompleksFayl entity = KompleksFayl.builder()
                    .kompleks(kompleks)
                    .faylNomi(fayl.getOriginalFilename())
                    .faylYoli("komplekslar/" + yangiNom)
                    .faylTuri(kengaytma)
                    .build();

            natija.add(kompleksFaylRepository.save(entity));
        }

        return natija;
    }

    private void faylniDiskdanOchirish(String faylYoli) {
        try {
            Files.deleteIfExists(Paths.get(uploadDir, faylYoli));
        } catch (IOException e) {
            // davom etamiz
        }
    }

    private void validateFaylHajmi(MultipartFile fayl, String kengaytma) {
        boolean videoMi = VIDEO_KENGAYTMALAR.contains(kengaytma);
        long maxHajm = videoMi ? VIDEO_MAX_SIZE : ODDIY_MAX_SIZE;

        if (fayl.getSize() > maxHajm) {
            String chegara = videoMi ? "50 MB" : "5 MB";
            throw new RuntimeException(
                    "'" + fayl.getOriginalFilename() + "' fayli hajmi " + chegara + " dan oshmasligi kerak");
        }
    }

    // XAVFSIZLIK: faqat harf/raqamdan iborat, qisqa kengaytma qabul qilinadi -
    // aks holda ("/" yoki ".." kabi belgilar bo'lsa) xato qaytariladi
    private String getFileExtension(String faylNomi) {
        if (faylNomi == null || !faylNomi.contains(".")) {
            throw new RuntimeException("Fayl nomi noto'g'ri");
        }
        String ext = faylNomi.substring(faylNomi.lastIndexOf(".") + 1).toLowerCase();
        if (ext.isEmpty() || ext.length() > 10 || !ext.matches("[a-z0-9]+")) {
            throw new RuntimeException("Fayl nomi noto'g'ri");
        }
        if (!com.edu.talim.config.FaylTurlari.HUJJAT.contains(ext)) {
            throw new RuntimeException("Ruxsat etilmagan fayl turi! Faqat quyidagilar qabul qilinadi: "
                    + String.join(", ", com.edu.talim.config.FaylTurlari.HUJJAT));
        }
        return ext;
    }

    private KompleksResponseDTO toDTO(Kompleks entity) {
        List<KompleksFaylDTO> faylDTOs = entity.getFayllar() == null ? List.of() :
                entity.getFayllar().stream()
                        .map(f -> KompleksFaylDTO.builder()
                                .id(f.getId())
                                .faylNomi(f.getFaylNomi())
                                .faylUrl(baseUrl + "/uploads/" + f.getFaylYoli())
                                .faylTuri(f.getFaylTuri())
                                .build())
                        .collect(Collectors.toList());

        return KompleksResponseDTO.builder()
                .id(entity.getId())
                .oqituvchiFanTaqsimlashId(entity.getOqituvchiFanTaqsimlash().getId())
                .fanNomi(entity.getOqituvchiFanTaqsimlash().getFanTaqsimlash().getFan().getFanNomi())
                .oqituvchiFISH(entity.getOqituvchiFanTaqsimlash().getOqituvchi().getFio())
                .kursNomi(entity.getOqituvchiFanTaqsimlash().getKurs().getKursRaqami() + "-kurs")
                .oquvYiliId(entity.getOquvYili().getId())
                .oquvYiliNomi(entity.getOquvYili().getNom())
                .materialNomi(entity.getMaterialNomi())
                .materialKategoriyasi(entity.getMaterialKategoriyasi())
                .biriktirilganVaqt(entity.getBiriktirilganVaqt())
                .fayllar(faylDTOs)
                .build();
    }
}