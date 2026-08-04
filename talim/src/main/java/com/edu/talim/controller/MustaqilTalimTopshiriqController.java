package com.edu.talim.controller;

import com.edu.talim.dto.*;
import com.edu.talim.entity.enums.TopshiriqHolati;
import com.edu.talim.service.MustaqilTalimTopshiriqService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mustaqil-talim-topshiriqlar")
@RequiredArgsConstructor
public class MustaqilTalimTopshiriqController {

    private final MustaqilTalimTopshiriqService topshiriqService;

    // Bitta mavzuga tegishli topshiriqlar ro'yxati
    @GetMapping
    public ResponseEntity<List<MustaqilTalimTopshiriqResponseDTO>> getByMavzu(
            @RequestParam Long darsJurnaliId) {
        return ResponseEntity.ok(topshiriqService.getByMavzu(darsJurnaliId));
    }

    // Bitta topshiriq (tahrirlash oynasi uchun)
    @GetMapping("/{id}")
    public ResponseEntity<MustaqilTalimTopshiriqResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(topshiriqService.getById(id));
    }

    // Yangi topshiriq yaratish (fayl(lar) bilan birga)
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<MustaqilTalimTopshiriqResponseDTO> create(
            @RequestParam Long oqituvchiFanTaqsimlashId,
            @RequestParam Long darsJurnaliId,
            @RequestParam String topshiriqTuri,
            @RequestParam String nomi,
            @RequestParam(required = false) String izoh,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime boshlanishSanasi,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime yakunlanishSanasi,
            @RequestParam(required = false) Integer urinishlarSoni,
            @RequestParam(required = false) List<MultipartFile> fayllar) throws IOException {
        return ResponseEntity.ok(topshiriqService.create(
                oqituvchiFanTaqsimlashId, darsJurnaliId, topshiriqTuri, nomi, izoh,
                boshlanishSanasi, yakunlanishSanasi, urinishlarSoni, fayllar));
    }

    // Topshiriqni yangilash
    @PutMapping("/{id}")
    public ResponseEntity<MustaqilTalimTopshiriqResponseDTO> update(
            @PathVariable Long id,
            @RequestParam(required = false) String topshiriqTuri,
            @RequestParam(required = false) String nomi,
            @RequestParam(required = false) String izoh,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime boshlanishSanasi,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime yakunlanishSanasi,
            @RequestParam(required = false) Integer urinishlarSoni) {
        return ResponseEntity.ok(topshiriqService.update(
                id, topshiriqTuri, nomi, izoh, boshlanishSanasi, yakunlanishSanasi, urinishlarSoni));
    }

    // Topshiriqni o'chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        topshiriqService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Topshiriqqa qo'shimcha fayl(lar) qo'shish
    @PostMapping(value = "/{id}/fayl", consumes = "multipart/form-data")
    public ResponseEntity<MustaqilTalimTopshiriqResponseDTO> faylQoshish(
            @PathVariable Long id,
            @RequestParam List<MultipartFile> fayllar) throws IOException {
        return ResponseEntity.ok(topshiriqService.faylQoshish(id, fayllar));
    }

    // Faylni o'chirish
    @DeleteMapping("/fayl/{faylId}")
    public ResponseEntity<Void> faylOchirish(@PathVariable Long faylId) {
        topshiriqService.faylOchirish(faylId);
        return ResponseEntity.noContent().build();
    }

    // Tanlangan kursantlarga topshiriqni yuborish
    @PostMapping("/yuborish")
    public ResponseEntity<MustaqilTalimTopshiriqResponseDTO> yuborish(
            @RequestBody TopshiriqYuborishRequestDTO dto) {
        return ResponseEntity.ok(topshiriqService.yuborish(dto));
    }

    // Topshiriq holati (barcha kursantlar, filtrlar bilan)
    @GetMapping("/holati")
    public ResponseEntity<TopshiriqHolatiSahifaDTO> getTopshiriqHolati(
            @RequestParam Long darsJurnaliId,
            @RequestParam(required = false) String guruhNomi,
            @RequestParam(required = false) TopshiriqHolati holati,
            @RequestParam(required = false) String topshiriqTuri) {
        return ResponseEntity.ok(
                topshiriqService.getTopshiriqHolati(darsJurnaliId, guruhNomi, holati, topshiriqTuri));
    }

    // ====================== Kursant tarafi ======================

    // Kursantga tegishli fan/mavzular ro'yxati (1-rasm)
    @GetMapping("/kursant/fanlar")
    public ResponseEntity<List<KursantFanMavzuDTO>> getFanlarVaMavzular(
            @RequestParam Long studentId) {
        return ResponseEntity.ok(topshiriqService.getFanlarVaMavzular(studentId));
    }

    // Kursantga shu mavzu bo'yicha yuborilgan topshiriqlar ro'yxati (2-rasm)
    @GetMapping("/kursant")
    public ResponseEntity<List<KursantTopshiriqDTO>> getMeningTopshiriqlarim(
            @RequestParam Long studentId,
            @RequestParam Long darsJurnaliId) {
        return ResponseEntity.ok(topshiriqService.getMeningTopshiriqlarim(studentId, darsJurnaliId));
    }

    // Bitta kursantning shu topshiriq bo'yicha javoblari (urinishlari)
    @GetMapping("/yuborish/{topshiriqYuborishId}/javoblar")
    public ResponseEntity<List<TopshiriqJavobResponseDTO>> getJavoblar(
            @PathVariable Long topshiriqYuborishId) {
        return ResponseEntity.ok(topshiriqService.getJavoblar(topshiriqYuborishId));
    }

    // Kursant tomonidan javob berish (izoh + fayl)
    @PostMapping(value = "/yuborish/{topshiriqYuborishId}/javob", consumes = "multipart/form-data")
    public ResponseEntity<TopshiriqJavobResponseDTO> javobBerish(
            @PathVariable Long topshiriqYuborishId,
            @RequestParam(required = false) String izoh,
            @RequestParam(required = false) MultipartFile fayl) throws IOException {
        return ResponseEntity.ok(topshiriqService.javobBerish(topshiriqYuborishId, izoh, fayl));
    }

    // O'qituvchi tomonidan baholamasdan qaytarish
    @PostMapping("/javob/{javobId}/qaytarish")
    public ResponseEntity<TopshiriqJavobResponseDTO> qaytarish(
            @PathVariable Long javobId,
            @RequestParam(required = false) String sabab) {
        return ResponseEntity.ok(topshiriqService.qaytarish(javobId, sabab));
    }

    // O'qituvchi tomonidan baholash
    @PutMapping("/javob/{javobId}/baholash")
    public ResponseEntity<TopshiriqJavobResponseDTO> baholash(
            @PathVariable Long javobId,
            @RequestBody BaholashRequestDTO dto) {
        return ResponseEntity.ok(topshiriqService.baholash(javobId, dto));
    }
}