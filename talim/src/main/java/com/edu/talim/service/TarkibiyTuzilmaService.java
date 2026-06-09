package com.edu.talim.service;

import com.edu.talim.entity.TarkibiyTuzilma;
import com.edu.talim.entity.enums.TarkibiyTuzilmaTuri;
import com.edu.talim.repository.TarkibiyTuzilmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TarkibiyTuzilmaService {

    private final TarkibiyTuzilmaRepository tarkibiyTuzilmaRepository;

    // Hammasini olish
    public List<TarkibiyTuzilma> getAll() {
        return tarkibiyTuzilmaRepository.findAll();
    }

    // Bo'limlarni olish
    public List<TarkibiyTuzilma> getBolimlar() {
        return tarkibiyTuzilmaRepository.findByTuri(TarkibiyTuzilmaTuri.BOLIM);
    }

    // Kafedralarni olish
    public List<TarkibiyTuzilma> getKafedralar() {
        return tarkibiyTuzilmaRepository.findByTuri(TarkibiyTuzilmaTuri.KAFEDRA);
    }

    // Qo'shish
    public TarkibiyTuzilma create(String nomi, String turi) {
        TarkibiyTuzilma tarkibiyTuzilma = TarkibiyTuzilma.builder()
                .nomi(nomi)
                .turi(TarkibiyTuzilmaTuri.valueOf(turi.toUpperCase()))
                .build();
        return tarkibiyTuzilmaRepository.save(tarkibiyTuzilma);
    }

    // O'chirish
    public void delete(Long id) {
        tarkibiyTuzilmaRepository.deleteById(id);
    }
}