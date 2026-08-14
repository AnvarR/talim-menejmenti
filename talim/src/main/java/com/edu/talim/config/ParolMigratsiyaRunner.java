package com.edu.talim.config;

import com.edu.talim.entity.Student;
import com.edu.talim.entity.User;
import com.edu.talim.repository.StudentRepository;
import com.edu.talim.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

// Bir martalik migratsiya: bazada hali ochiq (hash qilinmagan) holda saqlangan
// eski parollarni BCrypt formatiga o'tkazadi. Idempotent - allaqachon hashlangan
// ($2a$/$2b$/$2y$ bilan boshlangan) parollarga tegmaydi, shuning uchun har safar
// ilova ishga tushganda ishlatilsa ham xavfsiz (ikkinchi marta hech narsa topmaydi).
@Component
@RequiredArgsConstructor
@Slf4j
public class ParolMigratsiyaRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        int foydalanuvchilar = migratsiyaFoydalanuvchilar();
        int kursantlar = migratsiyaKursantlar();
        if (foydalanuvchilar > 0 || kursantlar > 0) {
            log.info("[Parol migratsiyasi] {} ta xodim, {} ta kursant/tinglovchi paroli BCrypt formatiga o'tkazildi",
                    foydalanuvchilar, kursantlar);
        }
    }

    private boolean hashlanganMi(String parol) {
        return parol != null && (parol.startsWith("$2a$") || parol.startsWith("$2b$") || parol.startsWith("$2y$"));
    }

    private int migratsiyaFoydalanuvchilar() {
        List<User> hammasi = userRepository.findAll();
        int soni = 0;
        for (User user : hammasi) {
            if (!hashlanganMi(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                soni++;
            }
        }
        return soni;
    }

    private int migratsiyaKursantlar() {
        List<Student> hammasi = studentRepository.findAll();
        int soni = 0;
        for (Student student : hammasi) {
            if (!hashlanganMi(student.getPassword())) {
                student.setPassword(passwordEncoder.encode(student.getPassword()));
                studentRepository.save(student);
                soni++;
            }
        }
        return soni;
    }
}