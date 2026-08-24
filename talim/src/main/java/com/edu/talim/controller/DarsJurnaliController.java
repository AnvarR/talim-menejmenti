package com.edu.talim.controller;

import java.util.UUID;

import com.edu.talim.dto.DarsJurnaliResponseDTO;
import com.edu.talim.dto.DavomatResponseDTO;
import com.edu.talim.dto.MavzuDTO;
import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.entity.enums.DavomatHolati;
import com.edu.talim.entity.enums.Semestr;
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
            @RequestParam UUID oqituvchiFanTaqsimlashId,
            @RequestParam DarsTuri darsTuri,
            @RequestParam Semestr semestr,
            @RequestParam Long oquvYiliId) {
        return ResponseEntity.ok(
                darsJurnaliService.getAll(oqituvchiFanTaqsimlashId, darsTuri, semestr, oquvYiliId));
    }

    // Bitta dars
    @GetMapping("/{id}")
    public ResponseEntity<DarsJurnaliResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(darsJurnaliService.getById(id));
    }

    // Yangi dars qo'shish (sana va semestr tanlanganda)
    @PostMapping
    public ResponseEntity<DarsJurnaliResponseDTO> create(
            @RequestParam UUID oqituvchiFanTaqsimlashId,
            @RequestParam DarsTuri darsTuri,
            @RequestParam Semestr semestr,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sana) {
        return ResponseEntity.ok(
                darsJurnaliService.create(oqituvchiFanTaqsimlashId, darsTuri, semestr, sana));
    }

    // Mavzu nomi va soatni yangilash
    @PutMapping("/{id}")
    public ResponseEntity<DarsJurnaliResponseDTO> update(
            @PathVariable UUID id,
            @RequestParam(required = false) String mavzuNomi,
            @RequestParam(required = false) Integer soat) {
        return ResponseEntity.ok(darsJurnaliService.update(id, mavzuNomi, soat));
    }

    // Dars sanasini o'zgartirish (cheklovsiz — istalgan vaqtda)
    @PutMapping("/{id}/sana")
    public ResponseEntity<DarsJurnaliResponseDTO> darsSanasiniOzgartirish(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate yangiSana) {
        return ResponseEntity.ok(darsJurnaliService.darsSanasiniOzgartirish(id, yangiSana));
    }

    // Darsni o'chirish (cheklovsiz — istalgan vaqtda)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darsniOchirish(@PathVariable UUID id) {
        darsJurnaliService.darsniOchirish(id);
        return ResponseEntity.noContent().build();
    }

    // Faqat mashg'ulot mavzulari ro'yxati (boshqa joylarda qayta ishlatish uchun yengil endpoint)
    @GetMapping("/mavzular")
    public ResponseEntity<List<MavzuDTO>> getMavzular(
            @RequestParam UUID oqituvchiFanTaqsimlashId,
            @RequestParam DarsTuri darsTuri,
            @RequestParam Semestr semestr,
            @RequestParam Long oquvYiliId) {
        return ResponseEntity.ok(
                darsJurnaliService.getMavzular(oqituvchiFanTaqsimlashId, darsTuri, semestr, oquvYiliId));
    }

    // Topshiriq fayl yuklash
    @PostMapping(value = "/{id}/topshiriq", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DarsJurnaliResponseDTO> topshiriqYuklash(
            @PathVariable UUID id,
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