package com.edu.talim.controller;

import java.util.UUID;

import com.edu.talim.dto.AmaliyotJurnalResponseDTO;
import com.edu.talim.service.AmaliyotJurnalService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/amaliyot-jurnal")
@RequiredArgsConstructor
public class AmaliyotJurnalController {

    private final AmaliyotJurnalService jurnalService;

    // Jurnalni ko'rish (semestrsiz - butun o'quv yili uchun)
    @GetMapping
    public ResponseEntity<AmaliyotJurnalResponseDTO> getJurnal(
            @RequestParam UUID oqituvchiFanTaqsimlashId,
            @RequestParam UUID oquvYiliId) {
        return ResponseEntity.ok(jurnalService.getJurnal(oqituvchiFanTaqsimlashId, oquvYiliId));
    }

    // Yangi amaliyot yaratish (faqat tugash sanasi)
    @PostMapping
    public ResponseEntity<Void> yaratish(
            @RequestParam UUID oqituvchiFanTaqsimlashId,
            @RequestParam UUID oquvYiliId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tugashSanasi) {
        jurnalService.yaratish(oqituvchiFanTaqsimlashId, oquvYiliId, tugashSanasi);
        return ResponseEntity.ok().build();
    }

    // Amaliyot tugash sanasini tahrirlash
    @PutMapping("/{amaliyotId}")
    public ResponseEntity<Void> tahrirlash(
            @PathVariable UUID amaliyotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tugashSanasi) {
        jurnalService.tahrirlash(amaliyotId, tugashSanasi);
        return ResponseEntity.ok().build();
    }

    // Amaliyotni o'chirish
    @DeleteMapping("/{amaliyotId}")
    public ResponseEntity<Void> ochirish(@PathVariable UUID amaliyotId) {
        jurnalService.ochirish(amaliyotId);
        return ResponseEntity.noContent().build();
    }

    // Baho qo'yish (2 olsa qayta topshirish avtomatik ishlaydi)
    @PutMapping("/baho/{amaliyotBahoId}")
    public ResponseEntity<Void> bahoQoyish(
            @PathVariable UUID amaliyotBahoId,
            @RequestParam Integer baho) {
        jurnalService.bahoQoyish(amaliyotBahoId, baho);
        return ResponseEntity.ok().build();
    }
}