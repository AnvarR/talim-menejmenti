package com.edu.talim.controller;

import java.util.UUID;

import com.edu.talim.dto.GroupResponseDTO;
import com.edu.talim.dto.StudentListDTO;
import com.edu.talim.entity.Group;
import com.edu.talim.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    // Barcha guruhlar (tinglovchi uchun)
    @GetMapping
    public ResponseEntity<List<Group>> getAll() {
        return ResponseEntity.ok(groupService.getAll());
    }

    // Kurs bo'yicha guruhlar
    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<Group>> getByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(groupService.getByCourseId(courseId));
    }

    // Faqat kursant guruhlari (fakultet boshlig'i uchun)
    @GetMapping("/kursant-guruhlari")
    public ResponseEntity<List<GroupResponseDTO>> getKursantGuruhlari() {
        return ResponseEntity.ok(groupService.getKursantGuruhlari());
    }

    // Guruhga biriktirilmagan kursantlar (kurs bo'yicha)
    @GetMapping("/biriktirilmagan-kursantlar")
    public ResponseEntity<List<StudentListDTO>> getBiriktirilmaganKursantlar(
            @RequestParam UUID kursId) {
        return ResponseEntity.ok(groupService.getBiriktirilmaganKursantlar(kursId));
    }

    // Yangi guruh yaratish
    @PostMapping
    public ResponseEntity<GroupResponseDTO> create(
            @RequestParam String guruhNomi,
            @RequestParam UUID courseId) {
        return ResponseEntity.ok(groupService.create(guruhNomi, courseId));
    }

    // Guruh nomini tahrirlash
    @PutMapping("/{id}")
    public ResponseEntity<GroupResponseDTO> update(
            @PathVariable UUID id,
            @RequestParam String guruhNomi) {
        return ResponseEntity.ok(groupService.update(id, guruhNomi));
    }

    // Guruhni o'chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Kursantni guruhga biriktirish
    @PutMapping("/{guruhId}/kursant-biriktirish/{studentId}")
    public ResponseEntity<Void> kursantBiriktirish(
            @PathVariable UUID guruhId,
            @PathVariable UUID studentId,
            @RequestParam(required = false) String reytingDaftarchasiRaqami) {
        groupService.kursantBiriktirish(guruhId, studentId, reytingDaftarchasiRaqami);
        return ResponseEntity.ok().build();
    }

    // Kursantni guruhdan chiqarish
    @DeleteMapping("/{guruhId}/kursant-chiqarish/{studentId}")
    public ResponseEntity<Void> kursantChiqarish(
            @PathVariable UUID guruhId,
            @PathVariable UUID studentId) {
        groupService.kursantChiqarish(guruhId, studentId);
        return ResponseEntity.noContent().build();
    }
}