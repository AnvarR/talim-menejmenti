package com.edu.talim.service;

import com.edu.talim.dto.InstitutdanChiqishCreateDTO;
import com.edu.talim.dto.InstitutdanChiqishResponseDTO;
import com.edu.talim.entity.InstitutdanChiqish;
import com.edu.talim.entity.Student;
import com.edu.talim.entity.enums.ChiqishSababi;
import com.edu.talim.repository.InstitutdanChiqishRepository;
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
public class InstitutdanChiqishService {

    private final InstitutdanChiqishRepository chiqishRepository;
    private final StudentRepository studentRepository;

    public Page<InstitutdanChiqishResponseDTO> getAll(
            String oquvYili,
            Integer kurs,
            String guruh,
            String fio,
            String chiqishSababi,
            String chiqganSana,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return chiqishRepository.findAllWithFilters(
                oquvYili, kurs, guruh, fio,
                chiqishSababi,
                parseDate(chiqganSana),
                pageable
        ).map(this::toResponseDTO);
    }

    public InstitutdanChiqishResponseDTO getById(Long id) {
        return toResponseDTO(findById(id));
    }

    public InstitutdanChiqishResponseDTO create(InstitutdanChiqishCreateDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Kursant topilmadi!"));

        InstitutdanChiqish chiqish = InstitutdanChiqish.builder()
                .student(student)
                .chiqishSababi(ChiqishSababi.valueOf(dto.getChiqishSababi()))
                .izoh(dto.getIzoh())
                .chiqganSana(parseDate(dto.getChiqganSana()))
                .chiqganVaqt(parseTime(dto.getChiqganVaqt()))
                .qaytganSana(parseDate(dto.getQaytganSana()))
                .qaytganVaqt(parseTime(dto.getQaytganVaqt()))
                .oquvYili(dto.getOquvYili())
                .build();

        return toResponseDTO(chiqishRepository.save(chiqish));
    }

    public InstitutdanChiqishResponseDTO update(Long id, InstitutdanChiqishCreateDTO dto) {
        InstitutdanChiqish chiqish = findById(id);

        chiqish.setChiqishSababi(ChiqishSababi.valueOf(dto.getChiqishSababi()));
        chiqish.setIzoh(dto.getIzoh());
        chiqish.setChiqganSana(parseDate(dto.getChiqganSana()));
        chiqish.setChiqganVaqt(parseTime(dto.getChiqganVaqt()));
        chiqish.setQaytganSana(parseDate(dto.getQaytganSana()));
        chiqish.setQaytganVaqt(parseTime(dto.getQaytganVaqt()));
        chiqish.setOquvYili(dto.getOquvYili());

        return toResponseDTO(chiqishRepository.save(chiqish));
    }

    public void delete(Long id) {
        chiqishRepository.delete(findById(id));
    }

    // ===== HELPER =====

    private InstitutdanChiqish findById(Long id) {
        return chiqishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chiqish topilmadi: " + id));
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
                return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception ex) {
                try {
                    return LocalTime.parse(time);
                } catch (Exception exc) {
                    return null;
                }
            }
        }
    }

    private InstitutdanChiqishResponseDTO toResponseDTO(InstitutdanChiqish c) {
        Student s = c.getStudent();
        return InstitutdanChiqishResponseDTO.builder()
                .id(c.getId())
                .studentId(s.getId())
                .fio(s.getFio())
                .kurs(s.getCourse() != null ? s.getCourse().getKursRaqami() + "-kurs" : null)
                .guruh(s.getGroup() != null ? s.getGroup().getGuruhNomi() : null)
                .photoUrl(s.getPhotoUrl())
                .chiqishSababi(c.getChiqishSababi() != null ? c.getChiqishSababi().getLabel() : null)
                .izoh(c.getIzoh())
                .chiqganSana(c.getChiqganSana() != null ? c.getChiqganSana().toString() : null)
                .chiqganVaqt(c.getChiqganVaqt() != null ? c.getChiqganVaqt().toString() : null)
                .qaytganSana(c.getQaytganSana() != null ? c.getQaytganSana().toString() : null)
                .qaytganVaqt(c.getQaytganVaqt() != null ? c.getQaytganVaqt().toString() : null)
                .oquvYili(c.getOquvYili())
                .createdAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null)
                .build();
    }
}