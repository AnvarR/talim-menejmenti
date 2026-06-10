package com.edu.talim.controller;

import com.edu.talim.dto.*;
import com.edu.talim.service.UserService;
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

    // Hammasini olish
    @GetMapping
    public ResponseEntity<List<UserDetailDTO>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    // Bitta user
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    // Qo'shish
    @PostMapping
    public ResponseEntity<UserDetailDTO> create(@RequestBody UserCreateDTO dto) {
        return ResponseEntity.ok(userService.create(dto));
    }

    // Rasm yuklash
    @PostMapping("/{id}/photo")
    public ResponseEntity<String> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(userService.uploadPhoto(id, file));
    }

    // Tahrirlash
    @PutMapping("/{id}")
    public ResponseEntity<UserDetailDTO> update(
            @PathVariable Long id,
            @RequestBody UserCreateDTO dto
    ) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    // O'chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Parol o'zgartirish
    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordDTO dto
    ) {
        userService.changePassword(id, dto);
        return ResponseEntity.ok().build();
    }

    // Telefon va email o'zgartirish
    @PutMapping("/{id}/update-contacts")
    public ResponseEntity<UserDetailDTO> updateContacts(
            @PathVariable Long id,
            @RequestBody UpdateContactsDTO dto
    ) {
        return ResponseEntity.ok(userService.updateContacts(id, dto));
    }
}