package com.edu.talim.controller;

import com.edu.talim.entity.TarkibiyTuzilma;
import com.edu.talim.service.TarkibiyTuzilmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tarkibiy-tuzilmalar")
@RequiredArgsConstructor
public class TarkibiyTuzilmaController {

    private final TarkibiyTuzilmaService tarkibiyTuzilmaService;

    // Hammasini olish
    @GetMapping
    public ResponseEntity<List<TarkibiyTuzilma>> getAll() {
        return ResponseEntity.ok(tarkibiyTuzilmaService.getAll());
    }

    // Faqat bo'limlar
    @GetMapping("/bolimlar")
    public ResponseEntity<List<TarkibiyTuzilma>> getBolimlar() {
        return ResponseEntity.ok(tarkibiyTuzilmaService.getBolimlar());
    }

    // Faqat kafedralar
    @GetMapping("/kafedralar")
    public ResponseEntity<List<TarkibiyTuzilma>> getKafedralar() {
        return ResponseEntity.ok(tarkibiyTuzilmaService.getKafedralar());
    }

    // Qo'shish
    @PostMapping
    public ResponseEntity<TarkibiyTuzilma> create(@RequestBody Map<String, String> body) {
        String nomi = body.get("nomi");
        String turi = body.get("turi");
        return ResponseEntity.ok(tarkibiyTuzilmaService.create(nomi, turi));
    }

    // O'chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tarkibiyTuzilmaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}