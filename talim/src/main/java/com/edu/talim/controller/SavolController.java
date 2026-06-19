package com.edu.talim.controller;

import com.edu.talim.dto.SavolCreateDTO;
import com.edu.talim.dto.SavolResponseDTO;
import com.edu.talim.dto.SavolStatistikaDTO;
import com.edu.talim.service.SavolService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/savollar")
@RequiredArgsConstructor
public class SavolController {

    private final SavolService savolService;

    // 4 ta karta uchun statistika
    @GetMapping("/statistika")
    public ResponseEntity<SavolStatistikaDTO> getStatistika() {
        return ResponseEntity.ok(savolService.getStatistika());
    }

    // Barcha savollar ro'yxati
    @GetMapping
    public ResponseEntity<Page<SavolResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(savolService.getAll(page, size));
    }

    // Bitta savol (korishlarSoni +1)
    @GetMapping("/{id}")
    public ResponseEntity<SavolResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(savolService.getById(id));
    }

    // Yangi savol yaratish
    @PostMapping
    public ResponseEntity<SavolResponseDTO> create(@RequestBody SavolCreateDTO dto) {
        return ResponseEntity.ok(savolService.create(dto));
    }

    // Savolga fayl yuklash — max 5 MB
    @PostMapping(value = "/{id}/fayl", consumes = "multipart/form-data")
    public ResponseEntity<SavolResponseDTO> uploadFayl(
            @PathVariable Long id,
            @RequestParam("fayl") MultipartFile fayl
    ) {
        return ResponseEntity.ok(savolService.uploadFayl(id, fayl));
    }

    // Savolni o'chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        savolService.delete(id);
        return ResponseEntity.noContent().build();
    }
}