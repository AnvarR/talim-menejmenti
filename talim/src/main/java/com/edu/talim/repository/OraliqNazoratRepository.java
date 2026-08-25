package com.edu.talim.repository;

import java.util.UUID;

import com.edu.talim.entity.OraliqNazorat;
import com.edu.talim.entity.enums.Semestr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OraliqNazoratRepository extends JpaRepository<OraliqNazorat, UUID> {

    // Kursantning shu fan, semestr va aniq oraliq (1 yoki 2) uchun nazorati
    Optional<OraliqNazorat> findByOqituvchiFanTaqsimlashIdAndStudentIdAndOquvYiliIdAndSemestrAndOraliqRaqami(
            UUID oqituvchiFanTaqsimlashId,
            UUID studentId,
            UUID oquvYiliId,
            Semestr semestr,
            Integer oraliqRaqami
    );

    // Barcha kursantlarning shu semestr va aniq oraliq (1 yoki 2) uchun baholari (jadval uchun)
    List<OraliqNazorat> findByOqituvchiFanTaqsimlashIdAndOquvYiliIdAndSemestrAndOraliqRaqami(
            UUID oqituvchiFanTaqsimlashId,
            UUID oquvYiliId,
            Semestr semestr,
            Integer oraliqRaqami
    );

    // Barcha kursantlarning shu semestrdagi ikkala oraliq bahosi (R(ON.SEM) hisoblash uchun)
    List<OraliqNazorat> findByOqituvchiFanTaqsimlashIdAndOquvYiliIdAndSemestr(
            UUID oqituvchiFanTaqsimlashId,
            UUID oquvYiliId,
            Semestr semestr
    );

    // Butun o'quv yili davomidagi (ikkala semestr, barcha oraliqlar) barcha yozuvlar.
    // R(KB) hisoblashda oldingi oraliqning kesim sanasini aniqlash uchun kerak
    // (masalan 2-semestr 1-oraliqining boshlanish nuqtasi = 1-semestr 2-oraliqining kesim sanasi).
    List<OraliqNazorat> findByOqituvchiFanTaqsimlashIdAndOquvYiliId(
            UUID oqituvchiFanTaqsimlashId,
            UUID oquvYiliId
    );

    // Reyting daftarchasi uchun: shu kursantning ma'lum semestrdagi barcha yozuvlari
    // (qaysi fan/taqsimlashlarda oraliq nazorati borligini aniqlash uchun)
    List<OraliqNazorat> findByStudentIdAndSemestr(UUID studentId, Semestr semestr);
}