package com.edu.talim.service;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.SavolCreateDTO;
import com.edu.talim.dto.SavolResponseDTO;
import com.edu.talim.dto.SavolStatistikaDTO;
import com.edu.talim.entity.Savol;
import com.edu.talim.repository.JavobRepository;
import com.edu.talim.repository.SavolRepository;
import com.edu.talim.repository.StudentRepository;
import com.edu.talim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SavolService {

    private final SavolRepository savolRepository;
    private final JavobRepository javobRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FileService fileService;

    // Barcha savollar — sahifalash bilan
    public Page<SavolResponseDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return savolRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toResponseDTO);
    }

    // Bitta savol — har ochilganda korishlarSoni +1 bo'ladi
    public SavolResponseDTO getById(Long id) {
        Savol savol = findById(id);
        savol.setKorishlarSoni(savol.getKorishlarSoni() + 1);
        savolRepository.save(savol);
        return toResponseDTO(savol);
    }

    // Yangi savol yaratish
    public SavolResponseDTO create(SavolCreateDTO dto) {
        Savol savol = Savol.builder()
                .authorId(dto.getAuthorId())
                .authorType(dto.getAuthorType())
                .mavzu(dto.getMavzu())
                .mazmun(dto.getMazmun())
                .faylUrl(dto.getFaylUrl())
                .korishlarSoni(0)
                .build();

        return toResponseDTO(savolRepository.save(savol));
    }

    // Savolga fayl yuklash — maksimum 5 MB
    public SavolResponseDTO uploadFayl(Long id, MultipartFile fayl) {
        // Fayl hajmini tekshirish: 5 MB = 5 * 1024 * 1024 bayt
        if (fayl.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("Fayl hajmi 5 MB dan oshmasligi kerak!");
        }

        Savol savol = findById(id);
        // Eski fayl bo'lsa o'chiriladi
        if (savol.getFaylUrl() != null) {
            fileService.deleteFile(savol.getFaylUrl());
        }

        // Yangi fayl saqlanadi
        String faylUrl = fileService.saveFile(fayl, com.edu.talim.config.FaylTurlari.HUJJAT_VA_RASM);
        savol.setFaylUrl(faylUrl);
        return toResponseDTO(savolRepository.save(savol));
    }

    // Savolni o'chirish
    public void delete(Long id) {
        Savol savol = findById(id);
        // Savol o'chirilganda fayli ham o'chiriladi
        if (savol.getFaylUrl() != null) {
            fileService.deleteFile(savol.getFaylUrl());
        }
        savolRepository.delete(savol);
    }

    // 4 ta karta uchun statistika
    public SavolStatistikaDTO getStatistika() {
        long barchaSavollar = savolRepository.count();
        long barchaJavoblar = javobRepository.count();
        long barchaKorishlar = savolRepository.sumKorishlarSoni();
        long yuklananMateriallar = savolRepository.countByFaylUrlIsNotNull();

        return SavolStatistikaDTO.builder()
                .barchaSavollar(barchaSavollar)
                .barchaJavoblar(barchaJavoblar)
                .barchaKorishlar(barchaKorishlar)
                .yuklananMateriallar(yuklananMateriallar)
                .build();
    }

    // ===== HELPER METODLAR =====

    private Savol findById(Long id) {
        return savolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Savol topilmadi: " + id));
    }

    // Savol -> ResponseDTO ga o'tkazish
    private SavolResponseDTO toResponseDTO(Savol s) {
        String authorFio = getFio(s.getAuthorId(), s.getAuthorType());
        String authorPhoto = getPhoto(s.getAuthorId(), s.getAuthorType());
        int javoblarSoni = javobRepository.countBySavolId(s.getId());

        return SavolResponseDTO.builder()
                .id(s.getId())
                .authorId(s.getAuthorId())
                .authorType(s.getAuthorType())
                .authorFio(authorFio)
                .authorPhoto(authorPhoto)
                .mavzu(s.getMavzu())
                .mazmun(s.getMazmun())
                .faylUrl(s.getFaylUrl())
                .javoblarSoni(javoblarSoni)
                .korishlarSoni(s.getKorishlarSoni() != null ? s.getKorishlarSoni() : 0)
                .createdAt(s.getCreatedAt() != null ? s.getCreatedAt().toString() : null)
                .build();
    }

    // authorType ga qarab F.I.SH olish: USER=xodim, STUDENT=kursant
    private String getFio(Long id, String type) {
        if ("USER".equals(type)) {
            return userRepository.findById(id).map(u -> u.getFio()).orElse("-");
        } else {
            return studentRepository.findById(id).map(s -> s.getFio()).orElse("-");
        }
    }

    // authorType ga qarab rasm URL olish
    private String getPhoto(Long id, String type) {
        if ("USER".equals(type)) {
            return userRepository.findById(id).map(u -> u.getPhotoUrl()).orElse(null);
        } else {
            return studentRepository.findById(id).map(s -> s.getPhotoUrl()).orElse(null);
        }
    }
}