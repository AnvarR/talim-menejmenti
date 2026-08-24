package com.edu.talim.service;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.XabarCreateDTO;
import com.edu.talim.dto.XabarResponseDTO;
import com.edu.talim.entity.Xabar;
import com.edu.talim.repository.StudentRepository;
import com.edu.talim.repository.UserRepository;
import com.edu.talim.repository.XabarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class XabarService {

    private final XabarRepository xabarRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    /** Xabar yuborish */
    public XabarResponseDTO send(XabarCreateDTO dto) {
        Xabar xabar = Xabar.builder()
                .senderId(dto.getSenderId())
                .senderType(dto.getSenderType())
                .receiverId(dto.getReceiverId())
                .receiverType(dto.getReceiverType())
                .mavzu(dto.getMavzu())
                .mazmun(dto.getMazmun())
                .oqilgan(false)
                .build();

        return toResponseDTO(xabarRepository.save(xabar));
    }

    /** Ikki foydalanuvchi o'rtasidagi suhbat */
    public List<XabarResponseDTO> getConversation(
            String userId, String userType,
            String otherId, String otherType
    ) {
        return xabarRepository.findConversation(userId, userType, otherId, otherType)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** Kiruvchi xabarlar ro'yxati */
    public Page<XabarResponseDTO> getInbox(
            String receiverId, String receiverType, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return xabarRepository
                .findByReceiverIdAndReceiverTypeOrderByCreatedAtDesc(
                        receiverId, receiverType, pageable)
                .map(this::toResponseDTO);
    }

    /** Xabarni o'qilgan deb belgilash */
    public void markAsRead(Long id) {
        Xabar xabar = xabarRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Xabar topilmadi: " + id));
        xabar.setOqilgan(true);
        xabarRepository.save(xabar);
    }

    /** O'qilmagan xabarlar soni */
    public long getUnreadCount(String receiverId, String receiverType) {
        return xabarRepository.countByReceiverIdAndReceiverTypeAndOqilgan(
                receiverId, receiverType, false);
    }

    /** Xabarni o'chirish */
    public void delete(Long id) {
        xabarRepository.deleteById(id);
    }

    // ===== HELPER =====

    private XabarResponseDTO toResponseDTO(Xabar x) {
        // Yuboruvchi ma'lumotlari
        String senderFio = getFio(x.getSenderId(), x.getSenderType());
        String senderPhoto = getPhoto(x.getSenderId(), x.getSenderType());

        // Qabul qiluvchi ma'lumotlari
        String receiverFio = getFio(x.getReceiverId(), x.getReceiverType());
        String receiverPhoto = getPhoto(x.getReceiverId(), x.getReceiverType());

        return XabarResponseDTO.builder()
                .id(x.getId())
                .senderId(x.getSenderId())
                .senderType(x.getSenderType())
                .senderFio(senderFio)
                .senderPhoto(senderPhoto)
                .receiverId(x.getReceiverId())
                .receiverType(x.getReceiverType())
                .receiverFio(receiverFio)
                .receiverPhoto(receiverPhoto)
                .mavzu(x.getMavzu())
                .mazmun(x.getMazmun())
                .oqilgan(x.getOqilgan())
                .createdAt(x.getCreatedAt() != null ? x.getCreatedAt().toString() : null)
                .build();
    }

    // id - matn ko'rinishida keladi: "USER" bo'lsa Long (masalan "9"), "STUDENT" bo'lsa UUID matni
    /** F.I.SH ni type ga qarab olish */
    private String getFio(String id, String type) {
        if ("USER".equals(type)) {
            return userRepository.findById(UUID.fromString(id))
                    .map(u -> u.getFio())
                    .orElse("-");
        } else {
            return studentRepository.findById(UUID.fromString(id))
                    .map(s -> s.getFio())
                    .orElse("-");
        }
    }

    /** Rasmni type ga qarab olish */
    private String getPhoto(String id, String type) {
        if ("USER".equals(type)) {
            return userRepository.findById(UUID.fromString(id))
                    .map(u -> u.getPhotoUrl())
                    .orElse(null);
        } else {
            return studentRepository.findById(UUID.fromString(id))
                    .map(s -> s.getPhotoUrl())
                    .orElse(null);
        }
    }
}