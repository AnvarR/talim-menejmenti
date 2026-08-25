package com.edu.talim.controller;

import java.util.UUID;

import com.edu.talim.dto.KasalCreateDTO;
import com.edu.talim.dto.KasalResponseDTO;
import com.edu.talim.service.KasalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kasallar")
@RequiredArgsConstructor
public class KasalController {

    private final KasalService kasalService;

    // Ro'yxat
    @GetMapping
    public ResponseEntity<Page<KasalResponseDTO>> getAll(
            @RequestParam(required = false) Integer kurs,
            @RequestParam(required = false) String guruh,
            @RequestParam(required = false) String fio,
            @RequestParam(required = false) String jinsi,
            @RequestParam(required = false) String kasalYuborilganJoy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(kasalService.getAll(
                kurs, guruh, fio, jinsi,
                kasalYuborilganJoy, page, size
        ));
    }

    // Bitta kasal
    @GetMapping("/{id}")
    public ResponseEntity<KasalResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(kasalService.getById(id));
    }

    // Qo'shish
    @PostMapping
    public ResponseEntity<KasalResponseDTO> create(@RequestBody KasalCreateDTO dto) {
        return ResponseEntity.ok(kasalService.create(dto));
    }

    // Tahrirlash
    @PutMapping("/{id}")
    public ResponseEntity<KasalResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody KasalCreateDTO dto
    ) {
        return ResponseEntity.ok(kasalService.update(id, dto));
    }

    // O'chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        kasalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}