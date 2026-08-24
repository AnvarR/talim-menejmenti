package com.edu.talim.service;

import java.util.UUID;

import com.edu.talim.exception.ConflictException;

import com.edu.talim.exception.NotFoundException;

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
    public Page<OqituvchiFanTaqsimlashResponseDTO> getByKafedra(UUID kafedraId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByFanTaqsimlashFanKafedraIdOrderByIdDesc(kafedraId, pageable)
                .map(this::toResponseDTO);
    }

    // ====================== Oraliq/Yakuniyga ruxsat ======================

    public Page<OqituvchiFanTaqsimlashResponseDTO> getOraliqYakuniyRuxsatRoyxati(
            UUID fanId, UUID oqituvchiId, UUID kursId, UUID guruhId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findOraliqYakuniyRuxsatRoyxati(fanId, oqituvchiId, kursId, guruhId, pageable)
                .map(this::toResponseDTO);
    }

    public OqituvchiFanTaqsimlashResponseDTO oraliqRuxsatBerish(Long id) {
        OqituvchiFanTaqsimlash t = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Taqsimlash topilmadi: " + id));
        t.setOraliqNazoratRuxsat(true);
        return toResponseDTO(repository.save(t));
    }

    public OqituvchiFanTaqsimlashResponseDTO yakuniyRuxsatBerish(Long id) {
        OqituvchiFanTaqsimlash t = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Taqsimlash topilmadi: " + id));
        t.setYakuniyNazoratRuxsat(true);
        return toResponseDTO(repository.save(t));
    }

    public OqituvchiFanTaqsimlashResponseDTO oraliqRuxsatBekorQilish(Long id) {
        OqituvchiFanTaqsimlash t = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Taqsimlash topilmadi: " + id));
        t.setOraliqNazoratRuxsat(false);
        return toResponseDTO(repository.save(t));
    }

    public OqituvchiFanTaqsimlashResponseDTO yakuniyRuxsatBekorQilish(Long id) {
        OqituvchiFanTaqsimlash t = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Taqsimlash topilmadi: " + id));
        t.setYakuniyNazoratRuxsat(false);
        return toResponseDTO(repository.save(t));
    }

    // Yangi taqsimlash yaratish
    public OqituvchiFanTaqsimlashResponseDTO create(OqituvchiFanTaqsimlashCreateDTO dto) {
        FanTaqsimlash fanTaqsimlash = fanTaqsimlashRepository.findById(dto.getFanTaqsimlashId())
                .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi!"));

        User oqituvchi = userRepository.findById(dto.getOqituvchiId())
                .orElseThrow(() -> new NotFoundException("O'qituvchi topilmadi!"));

        Course kurs = courseRepository.findById(dto.getKursId())
                .orElseThrow(() -> new NotFoundException("Kurs topilmadi!"));

        // Dars turini parse qilish
        DarsTuri darsTuri;
        try {
            darsTuri = DarsTuri.valueOf(dto.getDarsTuri().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Noto'g'ri dars turi: " + dto.getDarsTuri());
        }

        // Guruhlarni topish
        List<Group> guruhlar = dto.getGuruhIds().stream()
                .map(id -> groupRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Guruh topilmadi: " + id)))
                .collect(Collectors.toList());

        // Dublikat tekshiruvi - guruhlar ustma-ust tushadigan taqsimlash bormi
        List<OqituvchiFanTaqsimlash> ustmaUsht = repository.findGuruhlariUstmaUshtaTushganlar(
                dto.getFanTaqsimlashId(),
                dto.getOqituvchiId(),
                darsTuri,
                dto.getKursId(),
                dto.getGuruhIds(),
                null
        );
        if (!ustmaUsht.isEmpty()) {
            String guruhNomlari = ustmaUsht.stream()
                    .flatMap(t -> t.getGuruhlar().stream())
                    .map(Group::getGuruhNomi)
                    .distinct()
                    .collect(Collectors.joining(", "));
            throw new ConflictException("Bu taqsimlash allaqachon mavjud! (guruh: " + guruhNomlari + ")");
        }

        // Soat hajmini dars turidan avtomatik olish
        Integer soatHajmi = switch (darsTuri) {
            case MARUZA -> fanTaqsimlash.getMarruzaSoati();
            case SEMINAR -> fanTaqsimlash.getSeminarSoati();
            case MUSTAQIL_TALIM -> fanTaqsimlash.getMustaqilTalimSoati();
            // Amaliyotda soat hajmi umuman bo'lmaydi
            case AMALIYOT -> 0;
            // Kurs ishida esa soat hajmi qo'lda kiritiladi
            case KURS_ISHI -> {
                if (dto.getSoatHajmi() == null) {
                    throw new RuntimeException("Kurs ishi uchun soat hajmini kiriting!");
                }
                yield dto.getSoatHajmi();
            }
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
                .orElseThrow(() -> new NotFoundException("Fan taqsimlash topilmadi!"));

        User oqituvchi = userRepository.findById(dto.getOqituvchiId())
                .orElseThrow(() -> new NotFoundException("O'qituvchi topilmadi!"));

        Course kurs = courseRepository.findById(dto.getKursId())
                .orElseThrow(() -> new NotFoundException("Kurs topilmadi!"));

        DarsTuri darsTuri;
        try {
            darsTuri = DarsTuri.valueOf(dto.getDarsTuri().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Noto'g'ri dars turi: " + dto.getDarsTuri());
        }

        List<Group> guruhlar = dto.getGuruhIds().stream()
                .map(guruhId -> groupRepository.findById(guruhId)
                        .orElseThrow(() -> new NotFoundException("Guruh topilmadi: " + guruhId)))
                .collect(Collectors.toList());

        // Dublikat tekshiruvi (o'zini hisobga olmagan holda)
        List<OqituvchiFanTaqsimlash> ustmaUsht = repository.findGuruhlariUstmaUshtaTushganlar(
                dto.getFanTaqsimlashId(),
                dto.getOqituvchiId(),
                darsTuri,
                dto.getKursId(),
                dto.getGuruhIds(),
                id
        );
        if (!ustmaUsht.isEmpty()) {
            String guruhNomlari = ustmaUsht.stream()
                    .flatMap(t -> t.getGuruhlar().stream())
                    .map(Group::getGuruhNomi)
                    .distinct()
                    .collect(Collectors.joining(", "));
            throw new ConflictException("Bu taqsimlash allaqachon mavjud! (guruh: " + guruhNomlari + ")");
        }

        Integer soatHajmi = switch (darsTuri) {
            case MARUZA -> fanTaqsimlash.getMarruzaSoati();
            case SEMINAR -> fanTaqsimlash.getSeminarSoati();
            case MUSTAQIL_TALIM -> fanTaqsimlash.getMustaqilTalimSoati();
            // Amaliyotda soat hajmi umuman bo'lmaydi
            case AMALIYOT -> 0;
            // Kurs ishida esa soat hajmi qo'lda kiritiladi
            case KURS_ISHI -> {
                if (dto.getSoatHajmi() == null) {
                    throw new RuntimeException("Kurs ishi uchun soat hajmini kiriting!");
                }
                yield dto.getSoatHajmi();
            }
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
                .orElseThrow(() -> new NotFoundException("Taqsimlash topilmadi: " + id));
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
                .oraliqNazoratRuxsat(t.getOraliqNazoratRuxsat())
                .yakuniyNazoratRuxsat(t.getYakuniyNazoratRuxsat())
                .build();
    }
}