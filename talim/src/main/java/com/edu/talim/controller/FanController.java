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

    @GetMapping
    public ResponseEntity<Page<FanResponseDTO>> getAll(
            @RequestParam(required = false) Long kafedraId,
            @RequestParam(required = false) String fanNomi,
            @RequestParam(required = false) Long kursId,
            @RequestParam(required = false) Long guruhId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(fanService.getAll(kafedraId, fanNomi, kursId, guruhId, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FanResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fanService.getById(id));
    }

    @PostMapping
    public ResponseEntity<FanResponseDTO> create(@RequestBody FanCreateDTO dto) {
        return ResponseEntity.ok(fanService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FanResponseDTO> update(
            @PathVariable Long id,
            @RequestBody FanCreateDTO dto
    ) {
        return ResponseEntity.ok(fanService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}