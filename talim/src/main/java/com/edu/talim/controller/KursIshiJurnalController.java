package com.edu.talim.controller;

import java.util.UUID;

import com.edu.talim.dto.KursIshiJurnalResponseDTO;
import com.edu.talim.entity.enums.Semestr;
import com.edu.talim.service.KursIshiJurnalService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/kurs-ishi-jurnal")
@RequiredArgsConstructor
public class KursIshiJurnalController {

    private final KursIshiJurnalService jurnalService;

    // Jurnalni ko'rish
    @GetMapping
    public ResponseEntity<KursIshiJurnalResponseDTO> getJurnal(
            @RequestParam UUID oqituvchiFanTaqsimlashId,
            @RequestParam Semestr semestr,
            @RequestParam UUID oquvYiliId) {
        return ResponseEntity.ok(jurnalService.getJurnal(oqituvchiFanTaqsimlashId, semestr, oquvYiliId));
    }

    // Yangi kurs ishi yaratish (mavzu + umumiy topshirish muddati)
    @PostMapping
    public ResponseEntity<Void> yaratish(
            @RequestParam UUID oqituvchiFanTaqsimlashId,
            @RequestParam UUID oquvYiliId,
            @RequestParam Semestr semestr,
            @RequestParam String mavzuNomi,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate muddat) {
        jurnalService.yaratish(oqituvchiFanTaqsimlashId, oquvYiliId, semestr, mavzuNomi, muddat);
        return ResponseEntity.ok().build();
    }

    // Kurs ishi mavzusi/muddatini tahrirlash
    @PutMapping("/{kursIshiId}")
    public ResponseEntity<Void> tahrirlash(
            @PathVariable UUID kursIshiId,
            @RequestParam(required = false) String mavzuNomi,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate muddat) {
        jurnalService.tahrirlash(kursIshiId, mavzuNomi, muddat);
        return ResponseEntity.ok().build();
    }

    // Kurs ishini o'chirish
    @DeleteMapping("/{kursIshiId}")
    public ResponseEntity<Void> ochirish(@PathVariable UUID kursIshiId) {
        jurnalService.ochirish(kursIshiId);
        return ResponseEntity.noContent().build();
    }

    // Baho qo'yish (davomat/holat tushunchasisiz)
    @PutMapping("/baho/{kursIshiBahoId}")
    public ResponseEntity<Void> bahoQoyish(
            @PathVariable UUID kursIshiBahoId,
            @RequestParam Integer baho) {
        jurnalService.bahoQoyish(kursIshiBahoId, baho);
        return ResponseEntity.ok().build();
    }
}