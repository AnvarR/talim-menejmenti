package com.edu.talim.controller;

import com.edu.talim.dto.InstitutdanChiqishCreateDTO;
import com.edu.talim.dto.InstitutdanChiqishResponseDTO;
import com.edu.talim.service.InstitutdanChiqishService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/institutdan-chiqishlar")
@RequiredArgsConstructor
public class InstitutdanChiqishController {

    private final InstitutdanChiqishService chiqishService;

    @GetMapping
    public ResponseEntity<Page<InstitutdanChiqishResponseDTO>> getAll(
            @RequestParam(required = false) String oquvYili,
            @RequestParam(required = false) Integer kurs,
            @RequestParam(required = false) String guruh,
            @RequestParam(required = false) String fio,
            @RequestParam(required = false) String chiqishSababi,
            @RequestParam(required = false) String chiqganSana,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(chiqishService.getAll(
                oquvYili, kurs, guruh, fio,
                chiqishSababi, chiqganSana, page, size
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstitutdanChiqishResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(chiqishService.getById(id));
    }

    @PostMapping
    public ResponseEntity<InstitutdanChiqishResponseDTO> create(@RequestBody InstitutdanChiqishCreateDTO dto) {
        return ResponseEntity.ok(chiqishService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstitutdanChiqishResponseDTO> update(
            @PathVariable Long id,
            @RequestBody InstitutdanChiqishCreateDTO dto
    ) {
        return ResponseEntity.ok(chiqishService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        chiqishService.delete(id);
        return ResponseEntity.noContent().build();
    }
}