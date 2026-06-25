package com.edu.talim.controller;

import com.edu.talim.dto.FanCreateDTO;
import com.edu.talim.dto.FanResponseDTO;
import com.edu.talim.service.FanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fanlar")
@RequiredArgsConstructor
public class FanController {

    private final FanService fanService;

    // Barcha fanlar ro'yxati — sahifalash bilan
    @GetMapping
    public ResponseEntity<Page<FanResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(fanService.getAll(page, size));
    }

    // Bitta fan
    @GetMapping("/{id}")
    public ResponseEntity<FanResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fanService.getById(id));
    }

    // Yangi fan qo'shish
    @PostMapping
    public ResponseEntity<FanResponseDTO> create(@RequestBody FanCreateDTO dto) {
        return ResponseEntity.ok(fanService.create(dto));
    }

    // Fanni tahrirlash
    @PutMapping("/{id}")
    public ResponseEntity<FanResponseDTO> update(
            @PathVariable Long id,
            @RequestBody FanCreateDTO dto
    ) {
        return ResponseEntity.ok(fanService.update(id, dto));
    }

    // Fanni o'chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}