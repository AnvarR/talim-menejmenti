package com.edu.talim.service;

import com.edu.talim.dto.KasalCreateDTO;
import com.edu.talim.dto.KasalResponseDTO;
import com.edu.talim.entity.Kasal;
import com.edu.talim.entity.Student;
import com.edu.talim.entity.enums.KasalYuborilganJoy;
import com.edu.talim.repository.KasalRepository;
import com.edu.talim.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class KasalService {

    private final KasalRepository kasalRepository;
    private final StudentRepository studentRepository;

    // Ro'yxat
    public Page<KasalResponseDTO> getAll(
            Integer kurs,
            String guruh,
            String fio,
            String jinsi,
            String kasalYuborilganJoy,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return kasalRepository.findAllWithFilters(
                kurs, guruh, fio, jinsi,
                kasalYuborilganJoy, pageable
        ).map(this::toResponseDTO);
    }

    // Bitta kasal
    public KasalResponseDTO getById(Long id) {
        return toResponseDTO(findById(id));
    }

    // Qo'shish
    public KasalResponseDTO create(KasalCreateDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Kursant topilmadi!"));

        Kasal kasal = Kasal.builder()
                .student(student)
                .kasallikSababi(dto.getKasallikSababi())
                .kiritilganSana(parseDate(dto.getKiritilganSana()))
                .murojaatvaqti(parseTime(dto.getMurojaatvaqti()))
                .kasalYuborilganJoy(KasalYuborilganJoy.valueOf(dto.getKasalYuborilganJoy().toUpperCase()))
                .mutaxassisTuri(dto.getMutaxassisTuri())
                .boshlanishSanasi(parseDate(dto.getBoshlanishSanasi()))
                .tugashSanasi(parseDate(dto.getTugashSanasi()))
                .build();

        return toResponseDTO(kasalRepository.save(kasal));
    }

    // Tahrirlash
    public KasalResponseDTO update(Long id, KasalCreateDTO dto) {
        Kasal kasal = findById(id);

        kasal.setKasallikSababi(dto.getKasallikSababi());
        kasal.setKiritilganSana(parseDate(dto.getKiritilganSana()));
        kasal.setMurojaatvaqti(parseTime(dto.getMurojaatvaqti()));
        kasal.setKasalYuborilganJoy(KasalYuborilganJoy.valueOf(dto.getKasalYuborilganJoy().toUpperCase()));
        kasal.setMutaxassisTuri(dto.getMutaxassisTuri());
        kasal.setBoshlanishSanasi(parseDate(dto.getBoshlanishSanasi()));
        kasal.setTugashSanasi(parseDate(dto.getTugashSanasi()));

        return toResponseDTO(kasalRepository.save(kasal));
    }

    // O'chirish
    public void delete(Long id) {
        kasalRepository.delete(findById(id));
    }

    // ===== HELPER METODLAR =====

    private Kasal findById(Long id) {
        return kasalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kasal topilmadi: " + id));
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.isEmpty()) return null;
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            try {
                return LocalDate.parse(date);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private LocalTime parseTime(String time) {
        if (time == null || time.isEmpty()) return null;
        try {
            return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (Exception e) {
            try {
                return LocalTime.parse(time);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private KasalResponseDTO toResponseDTO(Kasal k) {
        Student s = k.getStudent();
        return KasalResponseDTO.builder()
                .id(k.getId())
                .studentId(s.getId())
                .kurs(s.getCourse() != null ? s.getCourse().getKursRaqami() + "-kurs" : null)
                .guruh(s.getGroup() != null ? s.getGroup().getGuruhNomi() : null)
                .fio(s.getFio())
                .passportMalumotlari(s.getPassportSeria())
                .jshshir(s.getJshshir())
                .tugilganSana(s.getTugilganSana() != null ? s.getTugilganSana().toString() : null)
                .jinsi(s.getJinsi() != null ? s.getJinsi().getLabel() : null)
                .photoUrl(s.getPhotoUrl())
                .kasallikSababi(k.getKasallikSababi())
                .kiritilganSana(k.getKiritilganSana() != null ? k.getKiritilganSana().toString() : null)
                .murojaatvaqti(k.getMurojaatvaqti() != null ? k.getMurojaatvaqti().toString() : null)
                .kasalYuborilganJoy(k.getKasalYuborilganJoy() != null ? k.getKasalYuborilganJoy().name() : null)
                .mutaxassisTuri(k.getMutaxassisTuri())
                .boshlanishSanasi(k.getBoshlanishSanasi() != null ? k.getBoshlanishSanasi().toString() : null)
                .tugashSanasi(k.getTugashSanasi() != null ? k.getTugashSanasi().toString() : null)
                .muddat(k.getMuddat())
                .createdAt(k.getCreatedAt() != null ? k.getCreatedAt().toString() : null)
                .build();
    }
}