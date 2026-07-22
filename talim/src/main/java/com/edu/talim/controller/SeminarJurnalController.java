package com.edu.talim.controller;

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
            @RequestParam Long oqituvchiFanTaqsimlashId,
            @RequestParam DarsTuri darsTuri,
            @RequestParam Semestr semestr,
            @RequestParam Long oquvYiliId) {
        return ResponseEntity.ok(
                elektronJurnalService.getJurnal(
                        oqituvchiFanTaqsimlashId, darsTuri, semestr, oquvYiliId));
    }

    // Yangi dars qo'shish (sana tanlanganda)
    @PostMapping("/dars")
    public ResponseEntity<DarsJurnaliResponseDTO> darsQoshish(
            @RequestParam Long oqituvchiFanTaqsimlashId,
            @RequestParam DarsTuri darsTuri,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sana) {
        return ResponseEntity.ok(
                elektronJurnalService.darsQoshish(
                        oqituvchiFanTaqsimlashId, darsTuri, sana));
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

    // Oraliq nazorat bahosini kiritish
    @PutMapping("/oraliq-nazorat")
    public ResponseEntity<Void> oraliqNazoratYangilash(
            @RequestParam Long oqituvchiFanTaqsimlashId,
            @RequestParam Long studentId,
            @RequestParam Long oquvYiliId,
            @RequestParam Semestr semestr,
            @RequestParam Integer baho) {
        elektronJurnalService.oraliqNazoratYangilash(
                oqituvchiFanTaqsimlashId, studentId, oquvYiliId, semestr, baho);
        return ResponseEntity.ok().build();
    }

    // Yakuniy nazorat bahosini kiritish
    @PutMapping("/yakuniy-nazorat")
    public ResponseEntity<Void> yakuniyNazoratYangilash(
            @RequestParam Long oqituvchiFanTaqsimlashId,
            @RequestParam Long studentId,
            @RequestParam Long oquvYiliId,
            @RequestParam Integer baho) {
        elektronJurnalService.yakuniyNazoratYangilash(
                oqituvchiFanTaqsimlashId, studentId, oquvYiliId, baho);
        return ResponseEntity.ok().build();
    }
}