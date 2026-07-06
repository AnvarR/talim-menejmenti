package com.edu.talim.controller;

import com.edu.talim.dto.KompleksResponseDTO;
import com.edu.talim.entity.enums.MaterialKategoriyasi;
import com.edu.talim.service.KompleksService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/komplekslar")
@RequiredArgsConstructor
public class KompleksController {

    private final KompleksService kompleksService;

    @GetMapping
    public ResponseEntity<List<KompleksResponseDTO>> getAll() {
        return ResponseEntity.ok(kompleksService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KompleksResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(kompleksService.getById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KompleksResponseDTO> create(
            @RequestParam Long oqituvchiFanTaqsimlashId,
            @RequestParam String materialNomi,
            @RequestParam MaterialKategoriyasi materialKategoriyasi,
            @RequestParam List<MultipartFile> fayllar) throws IOException {
        return ResponseEntity.ok(
                kompleksService.create(oqituvchiFanTaqsimlashId, materialNomi, materialKategoriyasi, fayllar));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KompleksResponseDTO> update(
            @PathVariable Long id,
            @RequestParam String materialNomi,
            @RequestParam MaterialKategoriyasi materialKategoriyasi,
            @RequestParam(required = false) List<MultipartFile> yangiFayllar) throws IOException {
        return ResponseEntity.ok(
                kompleksService.update(id, materialNomi, materialKategoriyasi, yangiFayllar));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        kompleksService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{kompleksId}/fayl/{faylId}")
    public ResponseEntity<Void> faylniOchirish(
            @PathVariable Long kompleksId, @PathVariable Long faylId) {
        kompleksService.faylniOchirish(kompleksId, faylId);
        return ResponseEntity.noContent().build();
    }
}