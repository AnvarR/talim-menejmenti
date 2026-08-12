package com.edu.talim.service;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.JavobCreateDTO;
import com.edu.talim.dto.JavobResponseDTO;
import com.edu.talim.entity.Javob;
import com.edu.talim.entity.Savol;
import com.edu.talim.repository.JavobRepository;
import com.edu.talim.repository.SavolRepository;
import com.edu.talim.repository.StudentRepository;
import com.edu.talim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JavobService {

    private final JavobRepository javobRepository;
    private final SavolRepository savolRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    /** Bitta savolga tegishli barcha javoblar */
    public List<JavobResponseDTO> getBySavolId(Long savolId) {
        return javobRepository.findBySavolIdOrderByCreatedAtAsc(savolId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** Javob berish */
    public JavobResponseDTO create(JavobCreateDTO dto) {
        Savol savol = savolRepository.findById(dto.getSavolId())
                .orElseThrow(() -> new NotFoundException("Savol topilmadi: " + dto.getSavolId()));

        Javob javob = Javob.builder()
                .savol(savol)
                .authorId(dto.getAuthorId())
                .authorType(dto.getAuthorType())
                .mazmun(dto.getMazmun())
                .build();

        return toResponseDTO(javobRepository.save(javob));
    }

    /** Javobni o'chirish */
    public void delete(Long id) {
        javobRepository.deleteById(id);
    }

    // ===== HELPER =====

    private JavobResponseDTO toResponseDTO(Javob j) {
        String authorFio = getFio(j.getAuthorId(), j.getAuthorType());
        String authorPhoto = getPhoto(j.getAuthorId(), j.getAuthorType());

        return JavobResponseDTO.builder()
                .id(j.getId())
                .savolId(j.getSavol().getId())
                .authorId(j.getAuthorId())
                .authorType(j.getAuthorType())
                .authorFio(authorFio)
                .authorPhoto(authorPhoto)
                .mazmun(j.getMazmun())
                .createdAt(j.getCreatedAt() != null ? j.getCreatedAt().toString() : null)
                .build();
    }

    private String getFio(Long id, String type) {
        if ("USER".equals(type)) {
            return userRepository.findById(id).map(u -> u.getFio()).orElse("-");
        } else {
            return studentRepository.findById(id).map(s -> s.getFio()).orElse("-");
        }
    }

    private String getPhoto(Long id, String type) {
        if ("USER".equals(type)) {
            return userRepository.findById(id).map(u -> u.getPhotoUrl()).orElse(null);
        } else {
            return studentRepository.findById(id).map(s -> s.getPhotoUrl()).orElse(null);
        }
    }
}