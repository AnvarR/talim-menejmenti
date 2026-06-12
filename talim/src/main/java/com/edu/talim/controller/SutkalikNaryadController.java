package com.edu.talim.controller;

import com.edu.talim.dto.SutkalikNaryadCreateDTO;
import com.edu.talim.dto.SutkalikNaryadResponseDTO;
import com.edu.talim.service.SutkalikNaryadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sutkalik-naryadlar")
@RequiredArgsConstructor
public class SutkalikNaryadController {

    private final SutkalikNaryadService naryadService;

    @GetMapping
    public ResponseEntity<Page<SutkalikNaryadResponseDTO>> getAll(
            @RequestParam(required = false) String oquvYili,
            @RequestParam(required = false) Integer kurs,
            @RequestParam(required = false) String guruh,
            @RequestParam(required = false) String fio,
            @RequestParam(required = false) String xizmatOtashJoyi,
            @RequestParam(required = false) String qabulQilishSanasi,
            @RequestParam(required = false) String topshirishSanasi,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(naryadService.getAll(
                oquvYili, kurs, guruh, fio,
                xizmatOtashJoyi, qabulQilishSanasi,
                topshirishSanasi, page, size
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SutkalikNaryadResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(naryadService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SutkalikNaryadResponseDTO> create(@RequestBody SutkalikNaryadCreateDTO dto) {
        return ResponseEntity.ok(naryadService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SutkalikNaryadResponseDTO> update(
            @PathVariable Long id,
            @RequestBody SutkalikNaryadCreateDTO dto
    ) {
        return ResponseEntity.ok(naryadService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        naryadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}