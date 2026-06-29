package com.edu.talim.service;

import com.edu.talim.dto.OqituvchiFanTaqsimlashCreateDTO;
import com.edu.talim.dto.OqituvchiFanTaqsimlashResponseDTO;
import com.edu.talim.entity.*;
import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OqituvchiFanTaqsimlashService {

    private final OqituvchiFanTaqsimlashRepository repository;
    private final FanTaqsimlashRepository fanTaqsimlashRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;

    // Kafedra bo'yicha taqsimlashlar ro'yxati
    public Page<OqituvchiFanTaqsimlashResponseDTO> getByKafedra(Long kafedraId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByFanTaqsimlashFanKafedraIdOrderByIdDesc(kafedraId, pageable)
                .map(this::toResponseDTO);
    }

    // Yangi taqsimlash yaratish
    public OqituvchiFanTaqsimlashResponseDTO create(OqituvchiFanTaqsimlashCreateDTO dto) {
        FanTaqsimlash fanTaqsimlash = fanTaqsimlashRepository.findById(dto.getFanTaqsimlashId())
                .orElseThrow(() -> new RuntimeException("Fan taqsimlash topilmadi!"));

        User oqituvchi = userRepository.findById(dto.getOqituvchiId())
                .orElseThrow(() -> new RuntimeException("O'qituvchi topilmadi!"));

        Course kurs = courseRepository.findById(dto.getKursId())
                .orElseThrow(() -> new RuntimeException("Kurs topilmadi!"));

        // Dars turini parse qilish
        DarsTuri darsTuri;
        try {
            darsTuri = DarsTuri.valueOf(dto.getDarsTuri().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Noto'g'ri dars turi: " + dto.getDarsTuri());
        }

        // Dublikat tekshiruvi
        boolean dublikat = repository.existsByFanTaqsimlashIdAndOqituvchiIdAndDarsTuriAndKursId(
                dto.getFanTaqsimlashId(),
                dto.getOqituvchiId(),
                darsTuri,
                dto.getKursId()
        );
        if (dublikat) {
            throw new RuntimeException("Bu taqsimlash allaqachon mavjud!");
        }

        // Guruhlarni topish
        List<Group> guruhlar = dto.getGuruhIds().stream()
                .map(id -> groupRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Guruh topilmadi: " + id)))
                .collect(Collectors.toList());

        // Soat hajmini dars turidan avtomatik olish
        Integer soatHajmi = switch (darsTuri) {
            case MARUZA -> fanTaqsimlash.getMarruzaSoati();
            case SEMINAR -> fanTaqsimlash.getSeminarSoati();
            case MUSTAQIL_TALIM -> fanTaqsimlash.getMustaqilTalimSoati();
        };

        OqituvchiFanTaqsimlash taqsimlash = OqituvchiFanTaqsimlash.builder()
                .fanTaqsimlash(fanTaqsimlash)
                .oqituvchi(oqituvchi)
                .darsTuri(darsTuri)
                .soatHajmi(soatHajmi)
                .kurs(kurs)
                .guruhlar(guruhlar)
                .build();

        return toResponseDTO(repository.save(taqsimlash));
    }

    // Taqsimlashni tahrirlash
    public OqituvchiFanTaqsimlashResponseDTO update(Long id, OqituvchiFanTaqsimlashCreateDTO dto) {
        OqituvchiFanTaqsimlash taqsimlash = findById(id);

        FanTaqsimlash fanTaqsimlash = fanTaqsimlashRepository.findById(dto.getFanTaqsimlashId())
                .orElseThrow(() -> new RuntimeException("Fan taqsimlash topilmadi!"));

        User oqituvchi = userRepository.findById(dto.getOqituvchiId())
                .orElseThrow(() -> new RuntimeException("O'qituvchi topilmadi!"));

        Course kurs = courseRepository.findById(dto.getKursId())
                .orElseThrow(() -> new RuntimeException("Kurs topilmadi!"));

        DarsTuri darsTuri;
        try {
            darsTuri = DarsTuri.valueOf(dto.getDarsTuri().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Noto'g'ri dars turi: " + dto.getDarsTuri());
        }

        List<Group> guruhlar = dto.getGuruhIds().stream()
                .map(guruhId -> groupRepository.findById(guruhId)
                        .orElseThrow(() -> new RuntimeException("Guruh topilmadi: " + guruhId)))
                .collect(Collectors.toList());

        Integer soatHajmi = switch (darsTuri) {
            case MARUZA -> fanTaqsimlash.getMarruzaSoati();
            case SEMINAR -> fanTaqsimlash.getSeminarSoati();
            case MUSTAQIL_TALIM -> fanTaqsimlash.getMustaqilTalimSoati();
        };

        taqsimlash.setFanTaqsimlash(fanTaqsimlash);
        taqsimlash.setOqituvchi(oqituvchi);
        taqsimlash.setDarsTuri(darsTuri);
        taqsimlash.setSoatHajmi(soatHajmi);
        taqsimlash.setKurs(kurs);
        taqsimlash.setGuruhlar(guruhlar);

        return toResponseDTO(repository.save(taqsimlash));
    }

    // Taqsimlashni o'chirish
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    // ===== HELPER METODLAR =====

    private OqituvchiFanTaqsimlash findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Taqsimlash topilmadi: " + id));
    }

    private OqituvchiFanTaqsimlashResponseDTO toResponseDTO(OqituvchiFanTaqsimlash t) {
        FanTaqsimlash ft = t.getFanTaqsimlash();

        return OqituvchiFanTaqsimlashResponseDTO.builder()
                .id(t.getId())
                .fanTaqsimlashId(ft.getId())
                .fanNomi(ft.getFan().getFanNomi())
                .kafedraNomi(ft.getFan().getKafedra().getNomi())
                .oqituvchiId(t.getOqituvchi().getId())
                .oqituvchiFio(t.getOqituvchi().getFio())
                .darsTuri(t.getDarsTuri().name())
                .soatHajmi(t.getSoatHajmi())
                .kursId(t.getKurs().getId())
                .kursRaqami(t.getKurs().getKursRaqami() + "-kurs")
                .guruhlar(t.getGuruhlar().stream()
                        .map(Group::getGuruhNomi)
                        .collect(Collectors.toList()))
                .build();
    }
}