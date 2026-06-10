package com.edu.talim.service;

import com.edu.talim.dto.*;
import com.edu.talim.entity.TarkibiyTuzilma;
import com.edu.talim.entity.User;
import com.edu.talim.entity.enums.*;
import com.edu.talim.repository.TarkibiyTuzilmaRepository;
import com.edu.talim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TarkibiyTuzilmaRepository tarkibiyTuzilmaRepository;
    private final FileService fileService;

    // Hammasini olish
    public List<UserDetailDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDetailDTO)
                .toList();
    }

    // Bitta user
    public UserDetailDTO getById(Long id) {
        return toDetailDTO(findById(id));
    }

    // Qo'shish
    public UserDetailDTO create(UserCreateDTO dto) {
        if (userRepository.existsByJshshir(dto.getJshshir())) {
            throw new RuntimeException("Bu JSHSHIR bilan foydalanuvchi allaqachon mavjud!");
        }
        User user = buildUser(dto);
        return toDetailDTO(userRepository.save(user));
    }

    // Rasm yuklash
    public String uploadPhoto(Long id, MultipartFile file) {
        User user = findById(id);
        if (user.getPhotoUrl() != null) {
            fileService.deleteFile(user.getPhotoUrl());
        }
        String photoUrl = fileService.saveFile(file);
        user.setPhotoUrl(photoUrl);
        userRepository.save(user);
        return photoUrl;
    }

    // Tahrirlash
    public UserDetailDTO update(Long id, UserCreateDTO dto) {
        User user = findById(id);
        updateUser(user, dto);
        return toDetailDTO(userRepository.save(user));
    }

    // O'chirish
    public void delete(Long id) {
        User user = findById(id);
        if (user.getPhotoUrl() != null) {
            fileService.deleteFile(user.getPhotoUrl());
        }
        userRepository.delete(user);
    }

    // Parol o'zgartirish
    public void changePassword(Long id, ChangePasswordDTO dto) {
        User user = findById(id);

        if (!user.getPassword().equals(dto.getHozirgiParol())) {
            throw new RuntimeException("Hozirgi parol noto'g'ri!");
        }
        if (!dto.getYangiParol().equals(dto.getYangiParolTakror())) {
            throw new RuntimeException("Yangi parollar mos kelmaydi!");
        }

        user.setPassword(dto.getYangiParol());
        userRepository.save(user);
    }

    // Telefon va email o'zgartirish
    public UserDetailDTO updateContacts(Long id, UpdateContactsDTO dto) {
        User user = findById(id);
        user.setTelefon1(dto.getTelefon1());
        user.setTelefon2(dto.getTelefon2());
        user.setPochtaManzili(dto.getEmail1());
        return toDetailDTO(userRepository.save(user));
    }

    // ===== HELPER METODLAR =====

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi: " + id));
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.isEmpty()) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(date, formatter);
        } catch (Exception e) {
            try {
                return LocalDate.parse(date);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private User buildUser(UserCreateDTO dto) {
        TarkibiyTuzilma tarkibiyTuzilma = null;
        if (dto.getTarkibiyTuzilmaId() != null) {
            tarkibiyTuzilma = tarkibiyTuzilmaRepository.findById(dto.getTarkibiyTuzilmaId())
                    .orElseThrow(() -> new RuntimeException("Tarkibiy tuzilma topilmadi"));
        }

        String username = dto.getPassportMalumotlari();
        String password = "12345678";

        return User.builder()
                .jshshir(dto.getJshshir())
                .passportMalumotlari(dto.getPassportMalumotlari())
                .tugilganSana(parseDate(dto.getTugilganSana()))
                .fuqaroligi(Fuqarolik.fromLabel(dto.getFuqaroligi()))
                .fio(dto.getFio())
                .hujjatBerilganSana(parseDate(dto.getHujjatBerilganSana()))
                .millati(Millat.fromLabel(dto.getMillati()))
                .telefon1(dto.getTelefon1())
                .telefon2(dto.getTelefon2())
                .malumoti(Malumot.fromLabel(dto.getMalumoti()))
                .jinsi(Jins.fromLabel(dto.getJinsi()))
                .hujjatBerganTashkilot(dto.getHujjatBerganTashkilot())
                .pochtaManzili(dto.getPochtaManzili())
                .tarkibiyTuzilma(tarkibiyTuzilma)
                .lavozimi(dto.getLavozimi())
                .ilmiyUnvoni(dto.getIlmiyUnvoni())
                .ilmiyDarajasi(dto.getIlmiyDarajasi())
                .guvohnomaNomeri(dto.getGuvohnomaNomeri())
                .harbiyUnvoni(dto.getHarbiyUnvoni())
                .role(dto.getRole() != null ? Role.valueOf(dto.getRole().toUpperCase()) : Role.RAHBARIYAT)
                .username(username)
                .password(password)
                .build();
    }

    private void updateUser(User user, UserCreateDTO dto) {
        user.setJshshir(dto.getJshshir());
        user.setPassportMalumotlari(dto.getPassportMalumotlari());
        user.setTugilganSana(parseDate(dto.getTugilganSana()));
        user.setFuqaroligi(Fuqarolik.fromLabel(dto.getFuqaroligi()));
        user.setFio(dto.getFio());
        user.setHujjatBerilganSana(parseDate(dto.getHujjatBerilganSana()));
        user.setMillati(Millat.fromLabel(dto.getMillati()));
        user.setTelefon1(dto.getTelefon1());
        user.setTelefon2(dto.getTelefon2());
        user.setMalumoti(Malumot.fromLabel(dto.getMalumoti()));
        user.setJinsi(Jins.fromLabel(dto.getJinsi()));
        user.setHujjatBerganTashkilot(dto.getHujjatBerganTashkilot());
        user.setPochtaManzili(dto.getPochtaManzili());
        user.setLavozimi(dto.getLavozimi());
        user.setIlmiyUnvoni(dto.getIlmiyUnvoni());
        user.setIlmiyDarajasi(dto.getIlmiyDarajasi());
        user.setGuvohnomaNomeri(dto.getGuvohnomaNomeri());
        user.setHarbiyUnvoni(dto.getHarbiyUnvoni());
        if (dto.getRole() != null) {
            user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        }
        if (dto.getTarkibiyTuzilmaId() != null) {
            TarkibiyTuzilma tarkibiyTuzilma = tarkibiyTuzilmaRepository
                    .findById(dto.getTarkibiyTuzilmaId())
                    .orElseThrow(() -> new RuntimeException("Tarkibiy tuzilma topilmadi"));
            user.setTarkibiyTuzilma(tarkibiyTuzilma);
        }
    }

    private UserDetailDTO toDetailDTO(User u) {
        return UserDetailDTO.builder()
                .id(u.getId())
                .photoUrl(u.getPhotoUrl())
                .jshshir(u.getJshshir())
                .passportMalumotlari(u.getPassportMalumotlari())
                .tugilganSana(u.getTugilganSana())
                .fuqaroligi(u.getFuqaroligi() != null ? u.getFuqaroligi().getLabel() : null)
                .fio(u.getFio())
                .hujjatBerilganSana(u.getHujjatBerilganSana())
                .millati(u.getMillati() != null ? u.getMillati().getLabel() : null)
                .telefon1(u.getTelefon1())
                .telefon2(u.getTelefon2())
                .malumoti(u.getMalumoti() != null ? u.getMalumoti().getLabel() : null)
                .jinsi(u.getJinsi() != null ? u.getJinsi().getLabel() : null)
                .hujjatBerganTashkilot(u.getHujjatBerganTashkilot())
                .pochtaManzili(u.getPochtaManzili())
                .tarkibiyTuzilmaId(u.getTarkibiyTuzilma() != null ? u.getTarkibiyTuzilma().getId() : null)
                .tarkibiyTuzilmaNomi(u.getTarkibiyTuzilma() != null ? u.getTarkibiyTuzilma().getNomi() : null)
                .lavozimi(u.getLavozimi())
                .ilmiyUnvoni(u.getIlmiyUnvoni())
                .ilmiyDarajasi(u.getIlmiyDarajasi())
                .guvohnomaNomeri(u.getGuvohnomaNomeri())
                .harbiyUnvoni(u.getHarbiyUnvoni())
                .role(u.getRole() != null ? u.getRole().name() : null)
                .username(u.getUsername())
                .createdAt(u.getCreatedAt())
                .build();
    }
}