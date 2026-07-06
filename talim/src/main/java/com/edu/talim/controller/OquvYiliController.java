package com.edu.talim.controller;

import com.edu.talim.dto.OquvYiliDTO;
import com.edu.talim.service.OquvYiliService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oquv-yillari")
@RequiredArgsConstructor
public class OquvYiliController {

    private final OquvYiliService oquvYiliService;

    @GetMapping
    public ResponseEntity<List<OquvYiliDTO>> getAll() {
        return ResponseEntity.ok(oquvYiliService.getAll());
    }

    @GetMapping("/faol")
    public ResponseEntity<OquvYiliDTO> getFaol() {
        return ResponseEntity.ok(oquvYiliService.getFaol());
    }

    @PostMapping
    public ResponseEntity<OquvYiliDTO> create(@RequestBody OquvYiliDTO dto) {
        return ResponseEntity.ok(oquvYiliService.create(dto));
    }

    @PutMapping("/{id}/faol-qilish")
    public ResponseEntity<OquvYiliDTO> faolQilish(@PathVariable Long id) {
        return ResponseEntity.ok(oquvYiliService.faolQilish(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        oquvYiliService.delete(id);
        return ResponseEntity.noContent().build();
    }
}