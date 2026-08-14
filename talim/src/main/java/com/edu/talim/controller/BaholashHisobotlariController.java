package com.edu.talim.controller;

import com.edu.talim.dto.HisobotSatriDTO;
import com.edu.talim.dto.KursGuruhHisobotDTO;
import com.edu.talim.dto.OgohlantirishRequestDTO;
import com.edu.talim.entity.enums.Semestr;
import com.edu.talim.service.BaholashHisobotlariService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/baholash-hisobotlari")
@RequiredArgsConstructor
public class BaholashHisobotlariController {

    private final BaholashHisobotlariService service;

    // 1) Individual baholar - bitta kursantning shu semestrdagi barcha fanlari
    @GetMapping("/individual")
    public ResponseEntity<List<HisobotSatriDTO>> individualBaholar(
            @RequestParam Long studentId,
            @RequestParam Long oquvYiliId,
            @RequestParam Semestr semestr) {
        return ResponseEntity.ok(service.individualBaholar(studentId, oquvYiliId, semestr));
    }

    // 2) Fan bo'yicha - bitta fandan butun guruhning hisoboti
    @GetMapping("/fan-boyicha")
    public ResponseEntity<List<HisobotSatriDTO>> fanBoyicha(
            @RequestParam Long fanId,
            @RequestParam Long kursId,
            @RequestParam Long guruhId,
            @RequestParam Long oquvYiliId,
            @RequestParam Semestr semestr) {
        return ResponseEntity.ok(service.fanBoyicha(fanId, kursId, guruhId, oquvYiliId, semestr));
    }

    // 3) Kurs/Guruh bo'yicha - guruhlar kesimida umumiy hisobot
    @GetMapping("/kurs-guruh-boyicha")
    public ResponseEntity<KursGuruhHisobotDTO> kursGuruhBoyicha(
            @RequestParam Long oquvYiliId,
            @RequestParam Semestr semestr,
            @RequestParam(required = false) Long kursId,
            @RequestParam(required = false) Long guruhId) {
        return ResponseEntity.ok(service.kursGuruhBoyicha(oquvYiliId, semestr, kursId, guruhId));
    }

    // 4) Past o'zlashtiruvchilar
    @GetMapping("/past-ozlashtiruvchilar")
    public ResponseEntity<List<HisobotSatriDTO>> pastOzlashtiruvchilar(
            @RequestParam Long oquvYiliId,
            @RequestParam Semestr semestr,
            @RequestParam(required = false) Long kursId,
            @RequestParam(required = false) Long guruhId,
            @RequestParam(required = false) Long fanId) {
        return ResponseEntity.ok(service.pastOzlashtiruvchilar(oquvYiliId, semestr, kursId, guruhId, fanId));
    }

    // Past o'zlashtiruvchilarga ogohlantirish yuborish
    @PostMapping("/ogohlantirish-yuborish")
    public ResponseEntity<Map<String, Object>> ogohlantirishYuborish(@RequestBody OgohlantirishRequestDTO dto) {
        int soni = service.ogohlantirishYuborish(dto);
        return ResponseEntity.ok(Map.of("yuborilganKursantlarSoni", soni));
    }
}