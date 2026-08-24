package com.edu.talim.controller;

import java.util.UUID;

import com.edu.talim.dto.ReytingDaftarchasiDTO;
import com.edu.talim.service.ReytingDaftarchasiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reyting-daftarchasi")
@RequiredArgsConstructor
public class ReytingDaftarchasiController {

    private final ReytingDaftarchasiService reytingDaftarchasiService;

    // globalSemestr: 1-8 (1-kurs 1/2-semestr = 1/2, 2-kurs = 3/4, 3-kurs = 5/6, 4-kurs = 7/8)
    @GetMapping
    public ResponseEntity<ReytingDaftarchasiDTO> getReyting(
            @RequestParam UUID studentId,
            @RequestParam Integer globalSemestr) {
        return ResponseEntity.ok(reytingDaftarchasiService.getReyting(studentId, globalSemestr));
    }
}