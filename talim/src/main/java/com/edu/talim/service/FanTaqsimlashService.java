package com.edu.talim.service;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.FanTaqsimlashCreateDTO;
import com.edu.talim.dto.FanTaqsimlashResponseDTO;
import com.edu.talim.entity.*;
import com.edu.talim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FanTaqsimlashService {

    private final FanTaqsimlashRepository fanTaqsimlashRepository;
    private final FanRepository fanRepository;
    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;

    // Barcha taqsimlashlar — sahifalash bilan
    public Page<FanTaqsimlashResponseDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fanTaqsimlashRepository.findAllByOrderByIdDesc(pageable)
                .map(this::toResponseDTO);
    }

    // Yangi taqsimlash yaratish
    public FanTaqsimlashResponseDTO create(FanTaqsimlashCreateDTO dto) {
        Fan fan = fanRepository.findById(dto.getFanId())
                .orElseThrow(() -> new NotFoundException("Fan topilmadi!"));

        Course kurs = courseRepository.findById(dto.getKursId())
                .orElseThrow(() -> new NotFoundException("Kurs topilmadi!"));

        Group guruh = groupRepository.findById(dto.getGuruhId())
                .orElseThrow(() -> new NotFoundException("Guruh topilmadi!"));

        // Dublikat tekshiruvi — hamma parametrlar bir xil bo'lsa xato qaytaradi
        boolean dublikat = fanTaqsimlashRepository
                .existsByFanIdAndKursIdAndGuruhIdAndSoatHajmiAndMarruzaSoatiAndSeminarSoatiAndMustaqilTalimSoatiAndAmaliyotMavjudAndKursIshiMavjud(
                        dto.getFanId(),
                        dto.getKursId(),
                        dto.getGuruhId(),
                        dto.getSoatHajmi(),
                        dto.getMarruzaSoati(),
                        dto.getSeminarSoati(),
                        dto.getMustaqilTalimSoati(),
                        dto.getAmaliyotMavjud() != null ? dto.getAmaliyotMavjud() : false,
                        dto.getKursIshiMavjud() != null ? dto.getKursIshiMavjud() : false
                );

        if (dublikat) {
            throw new RuntimeException("Siz buni oldin taqsimlagansiz!");
        }

        FanTaqsimlash taqsimlash = FanTaqsimlash.builder()
                .fan(fan)
                .kurs(kurs)
                .guruh(guruh)
                .soatHajmi(dto.getSoatHajmi())
                .marruzaSoati(dto.getMarruzaSoati())
                .seminarSoati(dto.getSeminarSoati())
                .mustaqilTalimSoati(dto.getMustaqilTalimSoati())
                .amaliyotMavjud(dto.getAmaliyotMavjud() != null ? dto.getAmaliyotMavjud() : false)
                .kursIshiMavjud(dto.getKursIshiMavjud() != null ? dto.getKursIshiMavjud() : false)
                .build();

        return toResponseDTO(fanTaqsimlashRepository.save(taqsimlash));
    }

    // Taqsimlashni tahrirlash
    public FanTaqsimlashResponseDTO update(Long id, FanTaqsimlashCreateDTO dto) {
        FanTaqsimlash taqsimlash = findById(id);

        Fan fan = fanRepository.findById(dto.getFanId())
                .orElseThrow(() -> new NotFoundException("Fan topilmadi!"));

        Course kurs = courseRepository.findById(dto.getKursId())
                .orElseThrow(() -> new NotFoundException("Kurs topilmadi!"));

        Group guruh = groupRepository.findById(dto.getGuruhId())
                .orElseThrow(() -> new NotFoundException("Guruh topilmadi!"));

        taqsimlash.setFan(fan);
        taqsimlash.setKurs(kurs);
        taqsimlash.setGuruh(guruh);
        taqsimlash.setSoatHajmi(dto.getSoatHajmi());
        taqsimlash.setMarruzaSoati(dto.getMarruzaSoati());
        taqsimlash.setSeminarSoati(dto.getSeminarSoati());
        taqsimlash.setMustaqilTalimSoati(dto.getMustaqilTalimSoati());
        taqsimlash.setAmaliyotMavjud(dto.getAmaliyotMavjud() != null ? dto.getAmaliyotMavjud() : false);
        taqsimlash.setKursIshiMavjud(dto.getKursIshiMavjud() != null ? dto.getKursIshiMavjud() : false);

        return toResponseDTO(fanTaqsimlashRepository.save(taqsimlash));
    }

    // Taqsimlashni o'chirish
    public void delete(Long id) {
        fanTaqsimlashRepository.delete(findById(id));
    }

    // ===== HELPER METODLAR =====

    private FanTaqsimlash findById(Long id) {
        return fanTaqsimlashRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Taqsimlash topilmadi: " + id));
    }

    private FanTaqsimlashResponseDTO toResponseDTO(FanTaqsimlash t) {
        return FanTaqsimlashResponseDTO.builder()
                .id(t.getId())
                .fanId(t.getFan().getId())
                .fanNomi(t.getFan().getFanNomi())
                .kafedraId(t.getFan().getKafedra().getId())
                .kafedraNomi(t.getFan().getKafedra().getNomi())
                .kursId(t.getKurs().getId())
                .kursRaqami(t.getKurs().getKursRaqami() + "-kurs")
                .guruhId(t.getGuruh().getId())
                .guruhNomi(t.getGuruh().getGuruhNomi())
                .soatHajmi(t.getSoatHajmi())
                .marruzaSoati(t.getMarruzaSoati())
                .seminarSoati(t.getSeminarSoati())
                .mustaqilTalimSoati(t.getMustaqilTalimSoati())
                .amaliyotMavjud(t.getAmaliyotMavjud())
                .kursIshiMavjud(t.getKursIshiMavjud())
                .build();
    }
}