package com.edu.talim.service;

import com.edu.talim.dto.SutkalikNaryadCreateDTO;
import com.edu.talim.dto.SutkalikNaryadResponseDTO;
import com.edu.talim.entity.SutkalikNaryad;
import com.edu.talim.entity.Student;
import com.edu.talim.entity.enums.XizmatOtashJoyi;
import com.edu.talim.repository.SutkalikNaryadRepository;
import com.edu.talim.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SutkalikNaryadService {

    private final SutkalikNaryadRepository naryadRepository;
    private final StudentRepository studentRepository;

    public Page<SutkalikNaryadResponseDTO> getAll(
            String oquvYili,
            Integer kurs,
            String guruh,
            String fio,
            String xizmatOtashJoyi,
            String qabulQilishSanasi,
            String topshirishSanasi,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        // Agar sana berilgan bo'lsa — sana bo'yicha filter
        if (qabulQilishSanasi != null && !qabulQilishSanasi.isEmpty()) {
            LocalDate sana = parseDate(qabulQilishSanasi);
            if (sana != null) {
                return naryadRepository.findByQabulQilishSanasi(sana, pageable)
                        .map(this::toResponseDTO);
            }
        }

        // Aks holda hammasi
        return naryadRepository.findAllRecords(pageable)
                .map(this::toResponseDTO);
    }

    public SutkalikNaryadResponseDTO getById(Long id) {
        return toResponseDTO(findById(id));
    }

    public SutkalikNaryadResponseDTO create(SutkalikNaryadCreateDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Kursant topilmadi!"));

        SutkalikNaryad naryad = SutkalikNaryad.builder()
                .student(student)
                .xizmatOtashJoyi(XizmatOtashJoyi.valueOf(dto.getXizmatOtashJoyi()))
                .qabulQilishSanasi(parseDate(dto.getQabulQilishSanasi()))
                .topshirishSanasi(parseDate(dto.getTopshirishSanasi()))
                .oquvYili(dto.getOquvYili())
                .build();

        return toResponseDTO(naryadRepository.save(naryad));
    }

    public SutkalikNaryadResponseDTO update(Long id, SutkalikNaryadCreateDTO dto) {
        SutkalikNaryad naryad = findById(id);

        naryad.setXizmatOtashJoyi(XizmatOtashJoyi.valueOf(dto.getXizmatOtashJoyi()));
        naryad.setQabulQilishSanasi(parseDate(dto.getQabulQilishSanasi()));
        naryad.setTopshirishSanasi(parseDate(dto.getTopshirishSanasi()));
        naryad.setOquvYili(dto.getOquvYili());

        return toResponseDTO(naryadRepository.save(naryad));
    }

    public void delete(Long id) {
        naryadRepository.delete(findById(id));
    }

    // ===== HELPER =====

    private SutkalikNaryad findById(Long id) {
        return naryadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Naryad topilmadi: " + id));
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

    private SutkalikNaryadResponseDTO toResponseDTO(SutkalikNaryad n) {
        Student s = n.getStudent();
        return SutkalikNaryadResponseDTO.builder()
                .id(n.getId())
                .studentId(s.getId())
                .fio(s.getFio())
                .kurs(s.getCourse() != null ? s.getCourse().getKursRaqami() + "-kurs" : null)
                .guruh(s.getGroup() != null ? s.getGroup().getGuruhNomi() : null)
                .photoUrl(s.getPhotoUrl())
                .xizmatOtashJoyi(n.getXizmatOtashJoyi() != null ? n.getXizmatOtashJoyi().getLabel() : null)
                .qabulQilishSanasi(n.getQabulQilishSanasi() != null ? n.getQabulQilishSanasi().toString() : null)
                .topshirishSanasi(n.getTopshirishSanasi() != null ? n.getTopshirishSanasi().toString() : null)
                .oquvYili(n.getOquvYili())
                .createdAt(n.getCreatedAt() != null ? n.getCreatedAt().toString() : null)
                .build();
    }
}