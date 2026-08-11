package com.edu.talim.service;

import com.edu.talim.dto.ElektronJurnalResponseDTO;
import com.edu.talim.dto.ReytingDaftarchasiDTO;
import com.edu.talim.entity.OqituvchiFanTaqsimlash;
import com.edu.talim.entity.OraliqNazorat;
import com.edu.talim.entity.Student;
import com.edu.talim.entity.YakuniyNazorat;
import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.entity.enums.Semestr;
import com.edu.talim.repository.OqituvchiFanTaqsimlashRepository;
import com.edu.talim.repository.OraliqNazoratRepository;
import com.edu.talim.repository.StudentRepository;
import com.edu.talim.repository.YakuniyNazoratRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReytingDaftarchasiService {

    private final OraliqNazoratRepository oraliqNazoratRepository;
    private final OqituvchiFanTaqsimlashRepository oqituvchiFanTaqsimlashRepository;
    private final YakuniyNazoratRepository yakuniyNazoratRepository;
    private final StudentRepository studentRepository;
    private final ElektronJurnalService elektronJurnalService;

    // Global semestr (1-8) -> kurs raqami (1-4)
    private int kursRaqamiHisobla(int globalSemestr) {
        return (globalSemestr + 1) / 2;
    }

    // Global semestr (1-8) -> shu kurs ichidagi 1- yoki 2-semestr
    private Semestr semestrHisobla(int globalSemestr) {
        return (globalSemestr % 2 == 1) ? Semestr.BIRINCHI : Semestr.IKKINCHI;
    }

    public ReytingDaftarchasiDTO getReyting(Long studentId, Integer globalSemestr) {
        if (globalSemestr == null || globalSemestr < 1 || globalSemestr > 8) {
            throw new RuntimeException("Semestr raqami 1 dan 8 gacha bo'lishi kerak!");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Kursant topilmadi: " + studentId));

        int kursRaqami = kursRaqamiHisobla(globalSemestr);
        Semestr semestr = semestrHisobla(globalSemestr);

        // Har bir "yozuv" - {taqsimlashId, oquvYiliId} juftligi. Fan bo'yicha SEMINAR
        // turidagi taqsimlash orqali aniqlanadi, chunki R(ON)/R(YN)/R(SEM) aynan shu yerda hisoblanadi.
        record TaqsimlashYil(Long taqsimlashId, Long oquvYiliId) {}

        Map<Long, TaqsimlashYil> taqsimlashlar = new LinkedHashMap<>();

        // 1) Shu semestrdagi oraliq nazoratlar orqali
        oraliqNazoratRepository.findByStudentIdAndSemestr(studentId, semestr).stream()
                .filter(on -> on.getOqituvchiFanTaqsimlash().getDarsTuri() == DarsTuri.SEMINAR)
                .filter(on -> on.getOqituvchiFanTaqsimlash().getKurs() != null
                        && kursRaqami == on.getOqituvchiFanTaqsimlash().getKurs().getKursRaqami())
                .forEach(on -> taqsimlashlar.putIfAbsent(
                        on.getOqituvchiFanTaqsimlash().getId(),
                        new TaqsimlashYil(on.getOqituvchiFanTaqsimlash().getId(), on.getOquvYili().getId())));

        // 2) Yakuniy nazoratlar orqali ham (YN semestrga bo'linmagan, butun yil uchun)
        yakuniyNazoratRepository.findByStudentId(studentId).stream()
                .filter(yn -> yn.getOqituvchiFanTaqsimlash().getDarsTuri() == DarsTuri.SEMINAR)
                .filter(yn -> yn.getOqituvchiFanTaqsimlash().getKurs() != null
                        && kursRaqami == yn.getOqituvchiFanTaqsimlash().getKurs().getKursRaqami())
                .forEach(yn -> taqsimlashlar.putIfAbsent(
                        yn.getOqituvchiFanTaqsimlash().getId(),
                        new TaqsimlashYil(yn.getOqituvchiFanTaqsimlash().getId(), yn.getOquvYili().getId())));

        List<ReytingDaftarchasiDTO.FanNatijaDTO> fanlar = taqsimlashlar.values().stream()
                .map(ty -> {
                    ElektronJurnalResponseDTO jurnal = elektronJurnalService.getJurnal(
                            ty.taqsimlashId(), DarsTuri.SEMINAR, semestr, ty.oquvYiliId());

                    Double rsem = jurnal.getKursantlar().stream()
                            .filter(k -> k.getStudentId().equals(studentId))
                            .findFirst()
                            .map(ElektronJurnalResponseDTO.KursantJurnalDTO::getRsem)
                            .orElse(null);

                    Integer soatHajmi = oqituvchiFanTaqsimlashRepository.findById(ty.taqsimlashId())
                            .map(t -> t.getFanTaqsimlash().getSoatHajmi())
                            .orElse(null);

                    return ReytingDaftarchasiDTO.FanNatijaDTO.builder()
                            .fanNomi(jurnal.getFanNomi())
                            .soatHajmi(soatHajmi)
                            .oqituvchiFio(jurnal.getOqituvchiFio())
                            .semestrBahosi(rsem)
                            .build();
                })
                .collect(Collectors.toList());

        return ReytingDaftarchasiDTO.builder()
                .studentId(student.getId())
                .studentFio(student.getFio())
                .globalSemestr(globalSemestr)
                .kursRaqami(kursRaqami)
                .fanlar(fanlar)
                .build();
    }
}