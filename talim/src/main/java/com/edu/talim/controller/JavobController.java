package com.edu.talim.controller;

import com.edu.talim.dto.JavobCreateDTO;
import com.edu.talim.dto.JavobResponseDTO;
import com.edu.talim.service.JavobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/javoblar")
@RequiredArgsConstructor
public class JavobController {

    private final JavobService javobService;

    /** Bitta savolga tegishli barcha javoblar */
    @GetMapping("/savol/{savolId}")
    public ResponseEntity<List<JavobResponseDTO>> getBySavolId(@PathVariable Long savolId) {
        return ResponseEntity.ok(javobService.getBySavolId(savolId));
    }

    /** Javob berish */
    @PostMapping
    public ResponseEntity<JavobResponseDTO> create(@RequestBody JavobCreateDTO dto) {
        return ResponseEntity.ok(javobService.create(dto));
    }

    /** Javobni o'chirish */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        javobService.delete(id);
        return ResponseEntity.noContent().build();
    }
}