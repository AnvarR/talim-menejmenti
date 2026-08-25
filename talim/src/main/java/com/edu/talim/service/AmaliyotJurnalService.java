package com.edu.talim.service;

import java.util.UUID;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.AmaliyotJurnalResponseDTO;
import com.edu.talim.entity.*;
import com.edu.talim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmaliyotJurnalService {

    private final AmaliyotRepository amaliyotRepository;
    private final AmaliyotBahoRepository amaliyotBahoRepository;
    private final OqituvchiFanTaqsimlashRepository oqituvchiFanTaqsimlashRepository;
    private final OquvYiliRepository oquvYiliRepository;
    private final StudentRepository studentRepository;
    private final OquvYiliService oquvYiliService;

    // ====================== Yaratish/tahrirlash/o'chirish ======================

    @Transactional
    public void yaratish(UUID oqituvchiFanTaqsimlashId, UUID oquvYiliId, LocalDate tugashSanasi) {
        OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi"));

        OquvYili oquvYili = oquvYiliRepository.findById(oquvYiliId)
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi"));

        oquvYiliService.tahririshniTekshir(oquvYiliId);

        Amaliyot amaliyot = Amaliyot.builder()
                .oqituvchiFanTaqsimlash(taqsimlash)
                .oquvYili(oquvYili)
                .tugashSanasi(tugashSanasi)
                .build();

        amaliyot = amaliyotRepository.save(amaliyot);

        List<Student> kursantlar = new ArrayList<>();
        if (taqsimlash.getGuruhlar() != null) {
            for (var guruh : taqsimlash.getGuruhlar()) {
                kursantlar.addAll(studentRepository.findByGroupIdOrderByFioAsc(guruh.getId()));
            }
        }

        for (Student student : kursantlar) {
            AmaliyotBaho baho = AmaliyotBaho.builder()
                    .amaliyot(amaliyot)
                    .student(student)
                    .baho(null)
                    .build();
            amaliyotBahoRepository.save(baho);
        }
    }

    @Transactional
    public void tahrirlash(UUID amaliyotId, LocalDate tugashSanasi) {
        Amaliyot amaliyot = amaliyotRepository.findById(amaliyotId)
                .orElseThrow(() -> new NotFoundException("Amaliyot topilmadi: " + amaliyotId));

        oquvYiliService.tahririshniTekshir(amaliyot.getOquvYili().getId());

        if (tugashSanasi != null) amaliyot.setTugashSanasi(tugashSanasi);

        amaliyotRepository.save(amaliyot);
    }

    @Transactional
    public void ochirish(UUID amaliyotId) {
        Amaliyot amaliyot = amaliyotRepository.findById(amaliyotId)
                .orElseThrow(() -> new NotFoundException("Amaliyot topilmadi: " + amaliyotId));
        oquvYiliService.tahririshniTekshir(amaliyot.getOquvYili().getId());
        amaliyotRepository.delete(amaliyot);
    }

    // ====================== Baholash ======================

    @Transactional
    public void bahoQoyish(UUID amaliyotBahoId, Integer baho) {
        if (baho == null || baho < 2 || baho > 5) {
            throw new RuntimeException("Baho 2, 3, 4 yoki 5 bo'lishi kerak!");
        }

        AmaliyotBaho amaliyotBaho = amaliyotBahoRepository.findById(amaliyotBahoId)
                .orElseThrow(() -> new NotFoundException("Amaliyot bahosi topilmadi: " + amaliyotBahoId));

        oquvYiliService.tahririshniTekshir(amaliyotBaho.getAmaliyot().getOquvYili().getId());

        // Agar birinchi baho allaqachon 2 bo'lsa - bu qayta topshirish bosqichi
        if (amaliyotBaho.getBaho() != null && amaliyotBaho.getBaho() == 2) {
            amaliyotBaho.setQaytaTopshirishBaho(baho);
        } else {
            amaliyotBaho.setBaho(baho);
        }

        amaliyotBahoRepository.save(amaliyotBaho);
    }

    // ====================== Jurnalni ko'rish ======================

    public AmaliyotJurnalResponseDTO getJurnal(UUID oqituvchiFanTaqsimlashId, UUID oquvYiliId) {

        OqituvchiFanTaqsimlash taqsimlash = oqituvchiFanTaqsimlashRepository
                .findById(oqituvchiFanTaqsimlashId)
                .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi"));

        OquvYili oquvYili = oquvYiliRepository.findById(oquvYiliId)
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi"));

        List<Amaliyot> amaliyotlar = amaliyotRepository
                .findByOqituvchiFanTaqsimlashIdAndOquvYiliIdOrderByTugashSanasiAsc(
                        oqituvchiFanTaqsimlashId, oquvYiliId);

        List<Student> kursantlar = new ArrayList<>();
        if (taqsimlash.getGuruhlar() != null) {
            for (var guruh : taqsimlash.getGuruhlar()) {
                kursantlar.addAll(studentRepository.findByGroupIdOrderByFioAsc(guruh.getId()));
            }
        }

        List<AmaliyotJurnalResponseDTO.KursantAmaliyotDTO> kursantDTOlar = kursantlar.stream()
                .map(student -> {
                    List<AmaliyotJurnalResponseDTO.BahoDTO> baholar = amaliyotlar.stream()
                            .map(am -> {
                                var bahoOpt = amaliyotBahoRepository
                                        .findByAmaliyotIdAndStudentId(am.getId(), student.getId());
                                return AmaliyotJurnalResponseDTO.BahoDTO.builder()
                                        .amaliyotBahoId(bahoOpt.map(AmaliyotBaho::getId).orElse(null))
                                        .baho(bahoOpt.map(AmaliyotBaho::getBaho).orElse(null))
                                        .qaytaTopshirishBaho(bahoOpt.map(AmaliyotBaho::getQaytaTopshirishBaho).orElse(null))
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return AmaliyotJurnalResponseDTO.KursantAmaliyotDTO.builder()
                            .studentId(student.getId())
                            .studentFio(student.getFio())
                            .baholar(baholar)
                            .build();
                })
                .collect(Collectors.toList());

        List<AmaliyotJurnalResponseDTO.AmaliyotUstunDTO> ustunlar = amaliyotlar.stream()
                .map(am -> AmaliyotJurnalResponseDTO.AmaliyotUstunDTO.builder()
                        .amaliyotId(am.getId())
                        .tugashSanasi(am.getTugashSanasi())
                        .build())
                .collect(Collectors.toList());

        String guruhNomi = taqsimlash.getGuruhlar() != null ?
                taqsimlash.getGuruhlar().stream()
                        .map(Group::getGuruhNomi)
                        .collect(Collectors.joining(", ")) : null;

        return AmaliyotJurnalResponseDTO.builder()
                .oqituvchiFanTaqsimlashId(oqituvchiFanTaqsimlashId)
                .fanNomi(taqsimlash.getFanTaqsimlash().getFan().getFanNomi())
                .oqituvchiFio(taqsimlash.getOqituvchi().getFio())
                .kursNomi(taqsimlash.getKurs().getKursRaqami() + "-kurs")
                .guruhNomi(guruhNomi)
                .oquvYiliNomi(oquvYili.getNom())
                .amaliyotlar(ustunlar)
                .kursantlar(kursantDTOlar)
                .build();
    }
}