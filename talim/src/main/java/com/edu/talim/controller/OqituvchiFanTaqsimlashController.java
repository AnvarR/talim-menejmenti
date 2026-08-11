package com.edu.talim.controller;

import com.edu.talim.dto.OqituvchiFanTaqsimlashCreateDTO;
import com.edu.talim.dto.OqituvchiFanTaqsimlashResponseDTO;
import com.edu.talim.service.OqituvchiFanTaqsimlashService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/oqituvchi-fan-taqsimlash")
@RequiredArgsConstructor
public class OqituvchiFanTaqsimlashController {

    private final OqituvchiFanTaqsimlashService service;

    // Kafedra bo'yicha taqsimlashlar ro'yxati
    @GetMapping
    public ResponseEntity<Page<OqituvchiFanTaqsimlashResponseDTO>> getByKafedra(
            @RequestParam Long kafedraId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(service.getByKafedra(kafedraId, page, size));
    }

    // Yangi taqsimlash yaratish
    @PostMapping
    public ResponseEntity<OqituvchiFanTaqsimlashResponseDTO> create(
            @RequestBody OqituvchiFanTaqsimlashCreateDTO dto
    ) {
        return ResponseEntity.ok(service.create(dto));
    }

    // Taqsimlashni tahrirlash
    @PutMapping("/{id}")
    public ResponseEntity<OqituvchiFanTaqsimlashResponseDTO> update(
            @PathVariable Long id,
            @RequestBody OqituvchiFanTaqsimlashCreateDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // Taqsimlashni o'chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ====================== Oraliq/Yakuniyga ruxsat ======================

    // Butun fakultet bo'yicha SEMINAR taqsimlashlar ro'yxati (filtrlar bilan)
    @GetMapping("/oraliq-yakuniy-ruxsat")
    public ResponseEntity<Page<OqituvchiFanTaqsimlashResponseDTO>> getOraliqYakuniyRuxsatRoyxati(
            @RequestParam(required = false) Long fanId,
            @RequestParam(required = false) Long oqituvchiId,
            @RequestParam(required = false) Long kursId,
            @RequestParam(required = false) Long guruhId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(service.getOraliqYakuniyRuxsatRoyxati(fanId, oqituvchiId, kursId, guruhId, page, size));
    }

    // Oraliq nazoratga ruxsat berish
    @PutMapping("/{id}/oraliq-ruxsat-berish")
    public ResponseEntity<OqituvchiFanTaqsimlashResponseDTO> oraliqRuxsatBerish(@PathVariable Long id) {
        return ResponseEntity.ok(service.oraliqRuxsatBerish(id));
    }

    // Yakuniy nazoratga ruxsat berish
    @PutMapping("/{id}/yakuniy-ruxsat-berish")
    public ResponseEntity<OqituvchiFanTaqsimlashResponseDTO> yakuniyRuxsatBerish(@PathVariable Long id) {
        return ResponseEntity.ok(service.yakuniyRuxsatBerish(id));
    }

    // Oraliq nazorat ruxsatini bekor qilish (yopish)
    @PutMapping("/{id}/oraliq-ruxsat-bekor-qilish")
    public ResponseEntity<OqituvchiFanTaqsimlashResponseDTO> oraliqRuxsatBekorQilish(@PathVariable Long id) {
        return ResponseEntity.ok(service.oraliqRuxsatBekorQilish(id));
    }

    // Yakuniy nazorat ruxsatini bekor qilish (yopish)
    @PutMapping("/{id}/yakuniy-ruxsat-bekor-qilish")
    public ResponseEntity<OqituvchiFanTaqsimlashResponseDTO> yakuniyRuxsatBekorQilish(@PathVariable Long id) {
        return ResponseEntity.ok(service.yakuniyRuxsatBekorQilish(id));
    }
}