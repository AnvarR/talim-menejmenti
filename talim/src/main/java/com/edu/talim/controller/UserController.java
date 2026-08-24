package com.edu.talim.controller;

import java.util.UUID;

import com.edu.talim.dto.*;
import com.edu.talim.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Barcha xodimlar
    @GetMapping
    public ResponseEntity<List<UserDetailDTO>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    // Kafedradagi o'qituvchilar ro'yxati — fan taqsimlashda o'qituvchi tanlash uchun
    @GetMapping("/oqituvchilar")
    public ResponseEntity<List<UserDetailDTO>> getOqituvchilar(
            @RequestParam Long kafedraId
    ) {
        return ResponseEntity.ok(userService.getOqituvchilar(kafedraId));
    }

    // Bitta user
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    // Qo'shish
    @PostMapping
    public ResponseEntity<UserDetailDTO> create(@Valid @RequestBody UserCreateDTO dto) {
        return ResponseEntity.ok(userService.create(dto));
    }

    // Rasm yuklash
    @PostMapping(value = "/{id}/photo", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(userService.uploadPhoto(id, file));
    }

    // Tahrirlash
    @PutMapping("/{id}")
    public ResponseEntity<UserDetailDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserCreateDTO dto
    ) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    // O'chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Parol o'zgartirish
    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordDTO dto
    ) {
        userService.changePassword(id, dto);
        return ResponseEntity.ok().build();
    }

    // Telefon va email o'zgartirish
    @PutMapping("/{id}/update-contacts")
    public ResponseEntity<UserDetailDTO> updateContacts(
            @PathVariable UUID id,
            @RequestBody UpdateContactsDTO dto
    ) {
        return ResponseEntity.ok(userService.updateContacts(id, dto));
    }
}