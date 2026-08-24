package com.edu.talim.controller;

import java.util.UUID;

import com.edu.talim.dto.MustaqilTalimJurnalResponseDTO;
import com.edu.talim.entity.enums.Semestr;
import com.edu.talim.service.MustaqilTalimJurnalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mustaqil-talim-jurnal")
@RequiredArgsConstructor
public class MustaqilTalimJurnalController {

    private final MustaqilTalimJurnalService jurnalService;

    @GetMapping
    public ResponseEntity<MustaqilTalimJurnalResponseDTO> getJurnal(
            @RequestParam UUID oqituvchiFanTaqsimlashId,
            @RequestParam Semestr semestr,
            @RequestParam Long oquvYiliId) {
        return ResponseEntity.ok(jurnalService.getJurnal(oqituvchiFanTaqsimlashId, semestr, oquvYiliId));
    }
}