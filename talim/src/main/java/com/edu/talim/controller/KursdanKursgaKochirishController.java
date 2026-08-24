package com.edu.talim.controller;

import java.util.UUID;

import com.edu.talim.dto.ArxivlashRequestDTO;
import com.edu.talim.dto.ChetlatishRequestDTO;
import com.edu.talim.dto.KochirishKursantDTO;
import com.edu.talim.dto.KochirishRequestDTO;
import com.edu.talim.dto.KochirishTarixiDTO;
import com.edu.talim.service.KursdanKursgaKochirishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kursdan-kursga-kochirish")
@RequiredArgsConstructor
public class KursdanKursgaKochirishController {

    private final KursdanKursgaKochirishService service;

    // Sahifadagi kursantlar ro'yxati: kurs (majburiy), guruh (ixtiyoriy),
    // o'quv yili (ixtiyoriy - Ruxsat/Tasdiqlash holatini aniqlash uchun)
    @GetMapping
    public ResponseEntity<List<KochirishKursantDTO>> getKursantlar(
            @RequestParam Long kursId,
            @RequestParam(required = false) Long guruhId,
            @RequestParam(required = false) Long oquvYiliId) {
        return ResponseEntity.ok(service.getKursantlar(kursId, guruhId, oquvYiliId));
    }

    // Tanlangan kursantlarni keyingi kursga ko'chirish
    @PostMapping("/kochirish")
    public ResponseEntity<Void> kochirish(@RequestBody KochirishRequestDTO dto) {
        service.kochirish(dto);
        return ResponseEntity.ok().build();
    }

    // Tanlangan kursantlarni o'qishdan chetlatish
    @PostMapping("/chetlatish")
    public ResponseEntity<Void> chetlatish(@RequestBody ChetlatishRequestDTO dto) {
        service.chetlatish(dto);
        return ResponseEntity.ok().build();
    }

    // 4-kurs bitiruvchilarini arxivlash
    @PostMapping("/arxivlash")
    public ResponseEntity<Void> arxivlash(@RequestBody ArxivlashRequestDTO dto) {
        service.arxivlash(dto);
        return ResponseEntity.ok().build();
    }

    // Bitta kursantning butun kurs-o'zgarish tarixi
    @GetMapping("/tarix/{studentId}")
    public ResponseEntity<List<KochirishTarixiDTO>> getTarix(@PathVariable UUID studentId) {
        return ResponseEntity.ok(service.getTarix(studentId));
    }
}