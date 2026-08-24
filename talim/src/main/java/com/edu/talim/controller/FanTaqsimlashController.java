package com.edu.talim.controller;

import java.util.UUID;

import com.edu.talim.dto.FanTaqsimlashCreateDTO;
import com.edu.talim.dto.FanTaqsimlashResponseDTO;
import com.edu.talim.service.FanTaqsimlashService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fan-taqsimlash")
@RequiredArgsConstructor
public class FanTaqsimlashController {

    private final FanTaqsimlashService fanTaqsimlashService;

    // Barcha taqsimlashlar ro'yxati — sahifalash bilan
    @GetMapping
    public ResponseEntity<Page<FanTaqsimlashResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(fanTaqsimlashService.getAll(page, size));
    }

    // Yangi taqsimlash yaratish
    @PostMapping
    public ResponseEntity<FanTaqsimlashResponseDTO> create(
            @RequestBody FanTaqsimlashCreateDTO dto
    ) {
        return ResponseEntity.ok(fanTaqsimlashService.create(dto));
    }

    // Taqsimlashni tahrirlash
    @PutMapping("/{id}")
    public ResponseEntity<FanTaqsimlashResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody FanTaqsimlashCreateDTO dto
    ) {
        return ResponseEntity.ok(fanTaqsimlashService.update(id, dto));
    }

    // Taqsimlashni o'chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        fanTaqsimlashService.delete(id);
        return ResponseEntity.noContent().build();
    }
}