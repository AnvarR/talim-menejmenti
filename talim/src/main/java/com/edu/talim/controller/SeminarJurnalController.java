package com.edu.talim.controller;

import java.util.UUID;

import com.edu.talim.dto.AmaliyDavomatResponseDTO;
import com.edu.talim.dto.DarsJurnaliResponseDTO;
import com.edu.talim.dto.ElektronJurnalResponseDTO;
import com.edu.talim.entity.enums.DarsTuri;
import com.edu.talim.entity.enums.DavomatHolati;
import com.edu.talim.entity.enums.Semestr;
import com.edu.talim.service.ElektronJurnalService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/seminar-jurnal")
@RequiredArgsConstructor
public class SeminarJurnalController {

    private final ElektronJurnalService elektronJurnalService;

    // To'liq elektron jurnal (kursantlar + baholar)
    @GetMapping
    public ResponseEntity<ElektronJurnalResponseDTO> getJurnal(
            @RequestParam UUID oqituvchiFanTaqsimlashId,
            @RequestParam DarsTuri darsTuri,
            @RequestParam Semestr semestr,
            @RequestParam UUID oquvYiliId) {
        return ResponseEntity.ok(
                elektronJurnalService.getJurnal(
                        oqituvchiFanTaqsimlashId, darsTuri, semestr, oquvYiliId));
    }

    // Yangi dars qo'shish (sana tanlanganda)
    @PostMapping("/dars")
    public ResponseEntity<DarsJurnaliResponseDTO> darsQoshish(
            @RequestParam UUID oqituvchiFanTaqsimlashId,
            @RequestParam DarsTuri darsTuri,
            @RequestParam Semestr semestr,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sana) {
        return ResponseEntity.ok(
                elektronJurnalService.darsQoshish(
                        oqituvchiFanTaqsimlashId, darsTuri, semestr, sana));
    }

    // Dars sanasini o'zgartirish (cheklovsiz — istalgan vaqtda)
    @PutMapping("/dars/{darsJurnaliId}")
    public ResponseEntity<DarsJurnaliResponseDTO> darsSanasiniOzgartirish(
            @PathVariable UUID darsJurnaliId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate yangiSana) {
        return ResponseEntity.ok(
                elektronJurnalService.darsSanasiniOzgartirish(darsJurnaliId, yangiSana));
    }

    // Darsni o'chirish (cheklovsiz — istalgan vaqtda)
    @DeleteMapping("/dars/{darsJurnaliId}")
    public ResponseEntity<Void> darsniOchirish(@PathVariable UUID darsJurnaliId) {
        elektronJurnalService.darsniOchirish(darsJurnaliId);
        return ResponseEntity.noContent().build();
    }

    // Davomat va baho yangilash
    @PutMapping("/davomat/{davomatId}")
    public ResponseEntity<AmaliyDavomatResponseDTO> davomatYangilash(
            @PathVariable Long davomatId,
            @RequestParam(required = false) DavomatHolati holat,
            @RequestParam(required = false) Integer baho) {
        return ResponseEntity.ok(
                elektronJurnalService.davomatYangilash(davomatId, holat, baho));
    }

    // Oraliq nazorat bahosini kiritish (oraliqRaqami: 1 yoki 2, kesimSanasi — o'qituvchi belgilaydi)
    @PutMapping("/oraliq-nazorat")
    public ResponseEntity<Void> oraliqNazoratYangilash(
            @RequestParam UUID oqituvchiFanTaqsimlashId,
            @RequestParam UUID studentId,
            @RequestParam UUID oquvYiliId,
            @RequestParam Semestr semestr,
            @RequestParam Integer oraliqRaqami,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate kesimSanasi,
            @RequestParam Integer baho) {
        elektronJurnalService.oraliqNazoratYangilash(
                oqituvchiFanTaqsimlashId, studentId, oquvYiliId, semestr,
                oraliqRaqami, kesimSanasi, baho);
        return ResponseEntity.ok().build();
    }

    // Yakuniy nazorat bahosini kiritish
    @PutMapping("/yakuniy-nazorat")
    public ResponseEntity<Void> yakuniyNazoratYangilash(
            @RequestParam UUID oqituvchiFanTaqsimlashId,
            @RequestParam UUID studentId,
            @RequestParam UUID oquvYiliId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sana,
            @RequestParam Integer baho) {
        elektronJurnalService.yakuniyNazoratYangilash(
                oqituvchiFanTaqsimlashId, studentId, oquvYiliId, sana, baho);
        return ResponseEntity.ok().build();
    }
}