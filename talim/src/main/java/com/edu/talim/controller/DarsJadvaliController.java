package com.edu.talim.controller;

import java.util.UUID;

import com.edu.talim.dto.DarsJadvaliResponseDTO;
import com.edu.talim.entity.enums.HaftaKuni;
import com.edu.talim.service.DarsJadvaliService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/dars-jadvali")
@RequiredArgsConstructor
public class DarsJadvaliController {

    private final DarsJadvaliService darsJadvaliService;

    @GetMapping
    public ResponseEntity<List<DarsJadvaliResponseDTO>> getAll(
            @RequestParam(required = false) UUID kursId,
            @RequestParam(required = false) UUID oquvYiliId,
            @RequestParam(required = false) HaftaKuni haftaKuni) {
        return ResponseEntity.ok(darsJadvaliService.getAll(kursId, oquvYiliId, haftaKuni));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DarsJadvaliResponseDTO> create(
            @RequestParam UUID kursId,
            @RequestParam UUID oquvYiliId,
            @RequestParam HaftaKuni haftaKuni,
            @RequestParam MultipartFile fayl) throws IOException {
        return ResponseEntity.ok(
                darsJadvaliService.create(kursId, oquvYiliId, haftaKuni, fayl));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        darsJadvaliService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/fayl")
    public ResponseEntity<Resource> getFayl(@PathVariable UUID id) throws IOException {
        Path faylYoli = darsJadvaliService.getFaylYoli(id);
        Resource resource = new UrlResource(faylYoli.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}