package com.edu.talim.service;

import com.edu.talim.exception.ConflictException;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.FanCreateDTO;
import com.edu.talim.dto.FanResponseDTO;
import com.edu.talim.entity.Fan;
import com.edu.talim.entity.TarkibiyTuzilma;
import com.edu.talim.entity.User;
import com.edu.talim.entity.enums.Role;
import com.edu.talim.repository.FanRepository;
import com.edu.talim.repository.TarkibiyTuzilmaRepository;
import com.edu.talim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FanService {

    private final FanRepository fanRepository;
    private final TarkibiyTuzilmaRepository tarkibiyTuzilmaRepository;
    private final UserRepository userRepository;

    // Barcha fanlar — sahifalash bilan
    public Page<FanResponseDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fanRepository.findAllByOrderByIdDesc(pageable)
                .map(this::toResponseDTO);
    }

    // Bitta fan
    public FanResponseDTO getById(Long id) {
        return toResponseDTO(findById(id));
    }

    // Yangi fan qo'shish
    // Yangi fan qo'shish
    public FanResponseDTO create(FanCreateDTO dto) {
        // Dublikat tekshiruvi — bir xil kafedra + fan nomi
        if (fanRepository.existsByKafedraIdAndFanNomi(dto.getKafedraId(), dto.getFanNomi())) {
            throw new ConflictException("Bu fan oldin qo'shilgan!");
        }

        TarkibiyTuzilma kafedra = tarkibiyTuzilmaRepository.findById(dto.getKafedraId())
                .orElseThrow(() -> new NotFoundException("Kafedra topilmadi!"));

        Fan fan = Fan.builder()
                .kafedra(kafedra)
                .fanNomi(dto.getFanNomi())
                .build();

        return toResponseDTO(fanRepository.save(fan));
    }

    // Fanni tahrirlash
    public FanResponseDTO update(Long id, FanCreateDTO dto) {
        Fan fan = findById(id);

        TarkibiyTuzilma kafedra = tarkibiyTuzilmaRepository.findById(dto.getKafedraId())
                .orElseThrow(() -> new NotFoundException("Kafedra topilmadi!"));

        fan.setKafedra(kafedra);
        fan.setFanNomi(dto.getFanNomi());

        return toResponseDTO(fanRepository.save(fan));
    }

    // Fanni o'chirish
    public void delete(Long id) {
        fanRepository.delete(findById(id));
    }

    // ===== HELPER METODLAR =====

    private Fan findById(Long id) {
        return fanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fan topilmadi: " + id));
    }

    // Fan → ResponseDTO ga o'tkazish
    private FanResponseDTO toResponseDTO(Fan f) {
        // Kafedra boshlig'ini avtomatik topish
        String kafedraBoshligiFio = userRepository
                .findByTarkibiyTuzilmaIdAndRole(f.getKafedra().getId(), Role.KAFEDRA_BOSHLIGHI)
                .map(User::getFio)
                .orElse("-");

        return FanResponseDTO.builder()
                .id(f.getId())
                .kafedraId(f.getKafedra().getId())
                .kafedraNomi(f.getKafedra().getNomi())
                .kafedraBoshligiFio(kafedraBoshligiFio)
                .fanNomi(f.getFanNomi())
                .build();
    }
}