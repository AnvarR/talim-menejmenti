package com.edu.talim.controller;

import com.edu.talim.dto.StudentCreateDTO;
import com.edu.talim.dto.StudentDetailDTO;
import com.edu.talim.dto.StudentListDTO;
import com.edu.talim.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // Ro'yxat
    @GetMapping
    public ResponseEntity<Page<StudentListDTO>> getAll(
            @RequestParam String type,
            @RequestParam(required = false) String oquvYili,
            @RequestParam(required = false) Integer kurs,
            @RequestParam(required = false) String guruh,
            @RequestParam(required = false) String fio,
            @RequestParam(required = false) String jinsi,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(
                studentService.getAll(type, oquvYili, kurs, guruh, fio, jinsi, page, size)
        );
    }

    // Bitta student
    @GetMapping("/{id}")
    public ResponseEntity<StudentDetailDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    // Qo'shish
    @PostMapping
    public ResponseEntity<StudentDetailDTO> create(@RequestBody StudentCreateDTO dto) {
        return ResponseEntity.ok(studentService.create(dto));
    }

    // Rasm yuklash
    @PostMapping("/{id}/photo")
    public ResponseEntity<String> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(studentService.uploadPhoto(id, file));
    }

    // Tahrirlash
    @PutMapping("/{id}")
    public ResponseEntity<StudentDetailDTO> update(
            @PathVariable Long id,
            @RequestBody StudentCreateDTO dto
    ) {
        return ResponseEntity.ok(studentService.update(id, dto));
    }

    // O'chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}