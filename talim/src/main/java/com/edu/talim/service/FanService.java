package com.edu.talim.service;

import com.edu.talim.dto.FanCreateDTO;
import com.edu.talim.dto.FanResponseDTO;
import com.edu.talim.entity.Fan;
import com.edu.talim.entity.TarkibiyTuzilma;
import com.edu.talim.entity.Course;
import com.edu.talim.entity.Group;
import com.edu.talim.entity.User;
import com.edu.talim.entity.enums.Role;
import com.edu.talim.repository.FanRepository;
import com.edu.talim.repository.TarkibiyTuzilmaRepository;
import com.edu.talim.repository.CourseRepository;
import com.edu.talim.repository.GroupRepository;
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
    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    /** Fanlar ro'yxatini filter bilan qaytaradi */
    public Page<FanResponseDTO> getAll(
            Long kafedraId,
            String fanNomi,
            Long kursId,
            Long guruhId,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return fanRepository.findAllWithFilters(
                kafedraId, fanNomi, kursId, guruhId, pageable
        ).map(this::toResponseDTO);
    }

    /** Bitta fan ma'lumotini qaytaradi */
    public FanResponseDTO getById(Long id) {
        return toResponseDTO(findById(id));
    }

    /** Yangi fan qo'shadi */
    public FanResponseDTO create(FanCreateDTO dto) {
        TarkibiyTuzilma kafedra = tarkibiyTuzilmaRepository.findById(dto.getKafedraId())
                .orElseThrow(() -> new RuntimeException("Kafedra topilmadi!"));

        Course kurs = courseRepository.findById(dto.getKursId())
                .orElseThrow(() -> new RuntimeException("Kurs topilmadi!"));

        Group guruh = groupRepository.findById(dto.getGuruhId())
                .orElseThrow(() -> new RuntimeException("Guruh topilmadi!"));

        Fan fan = Fan.builder()
                .kafedra(kafedra)
                .fanNomi(dto.getFanNomi())
                .soatHajmi(dto.getSoatHajmi())
                .kurs(kurs)
                .guruh(guruh)
                .marruzaSoati(dto.getMarruzaSoati())
                .seminarSoati(dto.getSeminarSoati())
                .mustaqilTalimSoati(dto.getMustaqilTalimSoati())
                .amaliyotMavjud(dto.getAmaliyotMavjud() != null ? dto.getAmaliyotMavjud() : false)
                .kursIshiMavjud(dto.getKursIshiMavjud() != null ? dto.getKursIshiMavjud() : false)
                .build();

        return toResponseDTO(fanRepository.save(fan));
    }

    /** Fan ma'lumotlarini yangilaydi */
    public FanResponseDTO update(Long id, FanCreateDTO dto) {
        Fan fan = findById(id);

        TarkibiyTuzilma kafedra = tarkibiyTuzilmaRepository.findById(dto.getKafedraId())
                .orElseThrow(() -> new RuntimeException("Kafedra topilmadi!"));

        Course kurs = courseRepository.findById(dto.getKursId())
                .orElseThrow(() -> new RuntimeException("Kurs topilmadi!"));

        Group guruh = groupRepository.findById(dto.getGuruhId())
                .orElseThrow(() -> new RuntimeException("Guruh topilmadi!"));

        fan.setKafedra(kafedra);
        fan.setFanNomi(dto.getFanNomi());
        fan.setSoatHajmi(dto.getSoatHajmi());
        fan.setKurs(kurs);
        fan.setGuruh(guruh);
        fan.setMarruzaSoati(dto.getMarruzaSoati());
        fan.setSeminarSoati(dto.getSeminarSoati());
        fan.setMustaqilTalimSoati(dto.getMustaqilTalimSoati());
        fan.setAmaliyotMavjud(dto.getAmaliyotMavjud() != null ? dto.getAmaliyotMavjud() : false);
        fan.setKursIshiMavjud(dto.getKursIshiMavjud() != null ? dto.getKursIshiMavjud() : false);

        return toResponseDTO(fanRepository.save(fan));
    }

    /** Fanni o'chiradi */
    public void delete(Long id) {
        fanRepository.delete(findById(id));
    }

    // ===== HELPER METODLAR =====

    private Fan findById(Long id) {
        return fanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fan topilmadi: " + id));
    }

    private FanResponseDTO toResponseDTO(Fan f) {
        // Kafedra boshlig'ini topish:
        // Tanlangan kafedra bo'yicha lavozimi "Kafedra boshlig'i" bo'lgan xodim topiladi
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
                .soatHajmi(f.getSoatHajmi())
                .kursId(f.getKurs().getId())
                .kursRaqami(f.getKurs().getKursRaqami() + "-kurs")
                .guruhId(f.getGuruh().getId())
                .guruhNomi(f.getGuruh().getGuruhNomi())
                .marruzaSoati(f.getMarruzaSoati())
                .seminarSoati(f.getSeminarSoati())
                .mustaqilTalimSoati(f.getMustaqilTalimSoati())
                .amaliyotMavjud(f.getAmaliyotMavjud())
                .kursIshiMavjud(f.getKursIshiMavjud())
                .createdAt(f.getCreatedAt() != null ? f.getCreatedAt().toString() : null)
                .build();
    }
}