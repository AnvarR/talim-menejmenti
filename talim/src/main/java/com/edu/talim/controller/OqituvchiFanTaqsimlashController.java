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
}