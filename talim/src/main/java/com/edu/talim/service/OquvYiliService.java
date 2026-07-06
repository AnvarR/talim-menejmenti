package com.edu.talim.service;

import com.edu.talim.dto.OquvYiliDTO;
import com.edu.talim.entity.OquvYili;
import com.edu.talim.repository.OquvYiliRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OquvYiliService {

    private final OquvYiliRepository oquvYiliRepository;

    public List<OquvYiliDTO> getAll() {
        return oquvYiliRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public OquvYiliDTO getFaol() {
        return oquvYiliRepository.findByFaolTrue()
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Faol o'quv yili topilmadi"));
    }

    @Transactional
    public OquvYiliDTO create(OquvYiliDTO dto) {
        if (oquvYiliRepository.existsByNom(dto.getNom())) {
            throw new RuntimeException("Bu o'quv yili allaqachon mavjud: " + dto.getNom());
        }
        if (oquvYiliRepository.existsByBoshlanishYilAndTugashYil(
                dto.getBoshlanishYil(), dto.getTugashYil())) {
            throw new RuntimeException("Bu yillar uchun o'quv yili allaqachon mavjud");
        }

        OquvYili entity = OquvYili.builder()
                .nom(dto.getNom())
                .boshlanishYil(dto.getBoshlanishYil())
                .tugashYil(dto.getTugashYil())
                .faol(false)
                .build();

        return toDTO(oquvYiliRepository.save(entity));
    }

    @Transactional
    public OquvYiliDTO faolQilish(Long id) {
        // Avval barcha yillarni faolsizlashtirish
        oquvYiliRepository.findByFaolTrue().ifPresent(y -> {
            y.setFaol(false);
            oquvYiliRepository.save(y);
        });

        OquvYili oquvYili = oquvYiliRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("O'quv yili topilmadi: " + id));

        oquvYili.setFaol(true);
        return toDTO(oquvYiliRepository.save(oquvYili));
    }

    @Transactional
    public void delete(Long id) {
        OquvYili oquvYili = oquvYiliRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("O'quv yili topilmadi: " + id));
        if (oquvYili.getFaol()) {
            throw new RuntimeException("Faol o'quv yilini o'chirib bo'lmaydi");
        }
        oquvYiliRepository.deleteById(id);
    }

    private OquvYiliDTO toDTO(OquvYili entity) {
        return OquvYiliDTO.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .boshlanishYil(entity.getBoshlanishYil())
                .tugashYil(entity.getTugashYil())
                .faol(entity.getFaol())
                .build();
    }
}