package com.edu.talim.service;

import com.edu.talim.exception.ConflictException;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.DarsJadvaliResponseDTO;
import com.edu.talim.entity.Course;
import com.edu.talim.entity.DarsJadvali;
import com.edu.talim.entity.OquvYili;
import com.edu.talim.entity.enums.HaftaKuni;
import com.edu.talim.repository.CourseRepository;
import com.edu.talim.repository.DarsJadvaliRepository;
import com.edu.talim.repository.OquvYiliRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DarsJadvaliService {

    private final DarsJadvaliRepository darsJadvaliRepository;
    private final CourseRepository courseRepository;
    private final OquvYiliRepository oquvYiliRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    public List<DarsJadvaliResponseDTO> getAll(UUID kursId, UUID oquvYiliId, HaftaKuni haftaKuni) {
        if (kursId != null && oquvYiliId != null && haftaKuni != null) {
            return darsJadvaliRepository
                    .findByKursIdAndOquvYiliIdAndHaftaKuni(kursId, oquvYiliId, haftaKuni)
                    .map(this::toDTO)
                    .map(List::of)
                    .orElse(List.of());
        } else if (kursId != null && oquvYiliId != null) {
            return darsJadvaliRepository.findByKursIdAndOquvYiliId(kursId, oquvYiliId)
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        return darsJadvaliRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DarsJadvaliResponseDTO create(UUID kursId, UUID oquvYiliId,
                                         HaftaKuni haftaKuni, MultipartFile fayl) throws IOException {

        if (darsJadvaliRepository.existsByKursIdAndOquvYiliIdAndHaftaKuni(
                kursId, oquvYiliId, haftaKuni)) {
            throw new ConflictException("Bu kurs, o'quv yili va hafta kuni uchun jadval allaqachon mavjud");
        }

        Course kurs = courseRepository.findById(kursId)
                .orElseThrow(() -> new NotFoundException("Kurs topilmadi: " + kursId));

        OquvYili oquvYili = oquvYiliRepository.findById(oquvYiliId)
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi: " + oquvYiliId));

        String faylTuri = getFileExtension(fayl.getOriginalFilename());
        validateFileType(faylTuri);

        String papka = uploadDir + "/dars-jadvali";
        Files.createDirectories(Paths.get(papka));

        String yangiNom = UUID.randomUUID() + "." + faylTuri;
        Path faylYoli = Paths.get(papka, yangiNom);
        Files.copy(fayl.getInputStream(), faylYoli, StandardCopyOption.REPLACE_EXISTING);

        DarsJadvali entity = DarsJadvali.builder()
                .kurs(kurs)
                .oquvYili(oquvYili)
                .haftaKuni(haftaKuni)
                .faylNomi(fayl.getOriginalFilename())
                .faylYoli("dars-jadvali/" + yangiNom)
                .faylTuri(faylTuri)
                .build();

        return toDTO(darsJadvaliRepository.save(entity));
    }

    @Transactional
    public void delete(UUID id) {
        DarsJadvali jadval = darsJadvaliRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Jadval topilmadi: " + id));

        try {
            Path faylYoli = Paths.get(uploadDir, jadval.getFaylYoli());
            Files.deleteIfExists(faylYoli);
        } catch (IOException e) {
            // fayl o'chirilmasa ham davom etamiz
        }

        darsJadvaliRepository.deleteById(id);
    }

    public Path getFaylYoli(UUID id) {
        DarsJadvali jadval = darsJadvaliRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Jadval topilmadi: " + id));
        return Paths.get(uploadDir, jadval.getFaylYoli());
    }

    private DarsJadvaliResponseDTO toDTO(DarsJadvali entity) {
        return DarsJadvaliResponseDTO.builder()
                .id(entity.getId())
                .kursId(entity.getKurs().getId())
                .kursNomi(entity.getKurs().getKursRaqami() + "-kurs")
                .oquvYiliId(entity.getOquvYili().getId())
                .oquvYiliNomi(entity.getOquvYili().getNom())
                .haftaKuni(entity.getHaftaKuni())
                .faylNomi(entity.getFaylNomi())
                .faylUrl(baseUrl + "/uploads/" + entity.getFaylYoli())
                .faylTuri(entity.getFaylTuri())
                .build();
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
        return ext;
    }

    private void validateFileType(String tur) {
        if (!List.of("pdf", "xlsx", "doc", "docx").contains(tur)) {
            throw new RuntimeException("Faqat pdf, xlsx, doc, docx formatlar qabul qilinadi");
        }
    }
}