package com.edu.talim.controller;

import com.edu.talim.dto.XabarCreateDTO;
import com.edu.talim.dto.XabarResponseDTO;
import com.edu.talim.service.XabarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/xabarlar")
@RequiredArgsConstructor
public class XabarController {

    private final XabarService xabarService;

    /** Xabar yuborish */
    @PostMapping
    public ResponseEntity<XabarResponseDTO> send(@RequestBody XabarCreateDTO dto) {
        return ResponseEntity.ok(xabarService.send(dto));
    }

    /** Ikki foydalanuvchi o'rtasidagi suhbat */
    @GetMapping("/conversation")
    public ResponseEntity<List<XabarResponseDTO>> getConversation(
            @RequestParam String userId,
            @RequestParam String userType,
            @RequestParam String otherId,
            @RequestParam String otherType
    ) {
        return ResponseEntity.ok(xabarService.getConversation(userId, userType, otherId, otherType));
    }

    /** Kiruvchi xabarlar */
    @GetMapping("/inbox")
    public ResponseEntity<Page<XabarResponseDTO>> getInbox(
            @RequestParam String receiverId,
            @RequestParam String receiverType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(xabarService.getInbox(receiverId, receiverType, page, size));
    }

    /** Xabarni o'qilgan deb belgilash */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        xabarService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /** O'qilmagan xabarlar soni */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @RequestParam String receiverId,
            @RequestParam String receiverType
    ) {
        return ResponseEntity.ok(xabarService.getUnreadCount(receiverId, receiverType));
    }

    /** Xabarni o'chirish */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        xabarService.delete(id);
        return ResponseEntity.noContent().build();
    }
}