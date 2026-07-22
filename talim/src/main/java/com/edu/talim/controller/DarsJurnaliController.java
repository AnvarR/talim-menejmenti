package com.edu.talim.controller;

import com.edu.talim.dto.DarsJurnaliResponseDTO;
import com.edu.talim.dto.DavomatResponseDTO;
import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.entity.enums.DavomatHolati;
import com.edu.talim.service.DarsJurnaliService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dars-jurnali")
@RequiredArgsConstructor
public class DarsJurnaliController {

    private final DarsJurnaliService darsJurnaliService;

    // O'qituvchiga tegishli darslar ro'yxati
    @GetMapping
    public ResponseEntity<List<DarsJurnaliResponseDTO>> getAll(
            @RequestParam Long oqituvchiFanTaqsimlashId,
            @RequestParam DarsTuri darsTuri,
            @RequestParam Long oquvYiliId) {
        return ResponseEntity.ok(
                darsJurnaliService.getAll(oqituvchiFanTaqsimlashId, darsTuri, oquvYiliId));
    }

    // Bitta dars
    @GetMapping("/{id}")
    public ResponseEntity<DarsJurnaliResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(darsJurnaliService.getById(id));
    }

    // Yangi dars qo'shish (sana tanlanganda)
    @PostMapping
    public ResponseEntity<DarsJurnaliResponseDTO> create(
            @RequestParam Long oqituvchiFanTaqsimlashId,
            @RequestParam DarsTuri darsTuri,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sana) {
        return ResponseEntity.ok(
                darsJurnaliService.create(oqituvchiFanTaqsimlashId, darsTuri, sana));
    }

    // Mavzu nomi va soatni yangilash
    @PutMapping("/{id}")
    public ResponseEntity<DarsJurnaliResponseDTO> update(
            @PathVariable Long id,
            @RequestParam(required = false) String mavzuNomi,
            @RequestParam(required = false) Integer soat) {
        return ResponseEntity.ok(darsJurnaliService.update(id, mavzuNomi, soat));
    }

    // Topshiriq fayl yuklash
    @PostMapping(value = "/{id}/topshiriq", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DarsJurnaliResponseDTO> topshiriqYuklash(
            @PathVariable Long id,
            @RequestParam MultipartFile fayl) throws IOException {
        return ResponseEntity.ok(darsJurnaliService.topshiriqYuklash(id, fayl));
    }

    // Davomat holatini yangilash (S, Y, N_T, K_T, S_T, Y_T)
    @PutMapping("/davomat/{davomatId}")
    public ResponseEntity<DavomatResponseDTO> davomatYangilash(
            @PathVariable Long davomatId,
            @RequestParam DavomatHolati holat) {
        return ResponseEntity.ok(darsJurnaliService.davomatYangilash(davomatId, holat));
    }
}