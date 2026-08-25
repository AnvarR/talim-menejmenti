package com.edu.talim.service;

import java.util.UUID;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.KursIshiJurnalResponseDTO;
import com.edu.talim.entity.*;
import com.edu.talim.entity.enums.Semestr;
import com.edu.talim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KursIshiJurnalService {

    private final KursIshiRepository kursIshiRepository;
    private final KursIshiBahoRepository kursIshiBahoRepository;
    private final OqituvchiFanTaqsimlashRepository oqituvchiFanTaqsimlashRepository;
    private final OquvYiliRepository oquvYiliRepository;
    private final StudentRepository studentRepository;
    private final OquvYiliService oquvYiliService;

    // ====================== Kurs ishi yaratish/tahrirlash/o'chirish ======================

    // Yangi kurs ishi yaratish (mavzu + umumiy muddat). Guruhdagi barcha kursantlar
    // uchun avtomatik ravishda bahosiz (baho=null) yozuvlar yaratiladi
    @Transactional
    public void yaratish(UUID oqituvchiFanTaqsimlashId, UUID oquvYiliId, Semestr semestr,
                         String mavzuNomi, LocalDate muddat) {
        OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi"));

        OquvYili oquvYili = oquvYiliRepository.findById(oquvYiliId)
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi"));

        oquvYiliService.tahririshniTekshir(oquvYiliId);

        KursIshi kursIshi = KursIshi.builder()
                .oqituvchiFanTaqsimlash(taqsimlash)
                .oquvYili(oquvYili)
                .semestr(semestr)
                .mavzuNomi(mavzuNomi)
                .muddat(muddat)
                .build();

        kursIshi = kursIshiRepository.save(kursIshi);

        List<Student> kursantlar = new ArrayList<>();
        if (taqsimlash.getGuruhlar() != null) {
            for (var guruh : taqsimlash.getGuruhlar()) {
                kursantlar.addAll(studentRepository.findByGroupIdOrderByFioAsc(guruh.getId()));
            }
        }

        for (Student student : kursantlar) {
            KursIshiBaho baho = KursIshiBaho.builder()
                    .kursIshi(kursIshi)
                    .student(student)
                    .baho(null)
                    .build();
            kursIshiBahoRepository.save(baho);
        }
    }

    // Kurs ishi mavzusi/muddatini tahrirlash
    @Transactional
    public void tahrirlash(UUID kursIshiId, String mavzuNomi, LocalDate muddat) {
        KursIshi kursIshi = kursIshiRepository.findById(kursIshiId)
                .orElseThrow(() -> new NotFoundException("Kurs ishi topilmadi: " + kursIshiId));

        oquvYiliService.tahririshniTekshir(kursIshi.getOquvYili().getId());

        if (mavzuNomi != null) kursIshi.setMavzuNomi(mavzuNomi);
        if (muddat != null) kursIshi.setMuddat(muddat);

        kursIshiRepository.save(kursIshi);
    }

    // Kurs ishini o'chirish (unga tegishli barcha baholar ham o'chadi)
    @Transactional
    public void ochirish(UUID kursIshiId) {
        KursIshi kursIshi = kursIshiRepository.findById(kursIshiId)
                .orElseThrow(() -> new NotFoundException("Kurs ishi topilmadi: " + kursIshiId));
        oquvYiliService.tahririshniTekshir(kursIshi.getOquvYili().getId());
        kursIshiRepository.delete(kursIshi);
    }

    // ====================== Baholash ======================

    @Transactional
    public void bahoQoyish(UUID kursIshiBahoId, Integer baho) {
        if (baho == null || baho < 2 || baho > 5) {
            throw new RuntimeException("Baho 2, 3, 4 yoki 5 bo'lishi kerak!");
        }

        KursIshiBaho kursIshiBaho = kursIshiBahoRepository.findById(kursIshiBahoId)
                .orElseThrow(() -> new NotFoundException("Kurs ishi bahosi topilmadi: " + kursIshiBahoId));

        oquvYiliService.tahririshniTekshir(kursIshiBaho.getKursIshi().getOquvYili().getId());

        // Agar birinchi baho allaqachon 2 bo'lsa - bu qayta topshirish bosqichi,
        // yangi baho asosiy "baho"ga emas, "qaytaTopshirishBaho"ga yoziladi
        if (kursIshiBaho.getBaho() != null && kursIshiBaho.getBaho() == 2) {
            kursIshiBaho.setQaytaTopshirishBaho(baho);
        } else {
            kursIshiBaho.setBaho(baho);
        }

        kursIshiBahoRepository.save(kursIshiBaho);
    }

    // ====================== Jurnalni ko'rish ======================

    public KursIshiJurnalResponseDTO getJurnal(UUID oqituvchiFanTaqsimlashId,
                                               Semestr semestr, UUID oquvYiliId) {

        OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi"));

        List<KursIshi> kursIshlari = kursIshiRepository
                .findByOqituvchiFanTaqsimlashIdAndOquvYiliIdAndSemestrOrderByMuddatAsc(
                        oqituvchiFanTaqsimlashId, oquvYiliId, semestr);

        List<Student> kursantlar = new ArrayList<>();
        if (taqsimlash.getGuruhlar() != null) {
            for (var guruh : taqsimlash.getGuruhlar()) {
                kursantlar.addAll(studentRepository.findByGroupIdOrderByFioAsc(guruh.getId()));
            }
        }

        List<KursIshiJurnalResponseDTO.KursantKursIshiDTO> kursantDTOlar = kursantlar.stream()
                .map(student -> {
                    List<KursIshiJurnalResponseDTO.BahoDTO> baholar = kursIshlari.stream()
                            .map(ki -> {
                                var bahoOpt = kursIshiBahoRepository
                                        .findByKursIshiIdAndStudentId(ki.getId(), student.getId());
                                return KursIshiJurnalResponseDTO.BahoDTO.builder()
                                        .kursIshiBahoId(bahoOpt.map(KursIshiBaho::getId).orElse(null))
                                        .baho(bahoOpt.map(KursIshiBaho::getBaho).orElse(null))
                                        .qaytaTopshirishBaho(bahoOpt.map(KursIshiBaho::getQaytaTopshirishBaho).orElse(null))
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return KursIshiJurnalResponseDTO.KursantKursIshiDTO.builder()
                            .studentId(student.getId())
                            .studentFio(student.getFio())
                            .baholar(baholar)
                            .build();
                })
                .collect(Collectors.toList());

        List<KursIshiJurnalResponseDTO.KursIshiUstunDTO> ustunlar = kursIshlari.stream()
                .map(ki -> KursIshiJurnalResponseDTO.KursIshiUstunDTO.builder()
                        .kursIshiId(ki.getId())
                        .mavzuNomi(ki.getMavzuNomi())
                        .muddat(ki.getMuddat())
                        .build())
                .collect(Collectors.toList());

        String guruhNomi = taqsimlash.getGuruhlar() != null ?
                taqsimlash.getGuruhlar().stream()
                        .map(Group::getGuruhNomi)
                        .collect(Collectors.joining(", ")) : null;

        return KursIshiJurnalResponseDTO.builder()
                .oqituvchiFanTaqsimlashId(oqituvchiFanTaqsimlashId)
                .fanNomi(taqsimlash.getFanTaqsimlash().getFan().getFanNomi())
                .oqituvchiFio(taqsimlash.getOqituvchi().getFio())
                .kursNomi(taqsimlash.getKurs().getKursRaqami() + "-kurs")
                .guruhNomi(guruhNomi)
                .semestr(semestr)
                .kursIshlari(ustunlar)
                .kursantlar(kursantDTOlar)
                .build();
    }
}