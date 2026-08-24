package com.edu.talim.service;

import java.util.UUID;

import com.edu.talim.exception.ConflictException;
import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.ArxivlashRequestDTO;
import com.edu.talim.dto.ChetlatishRequestDTO;
import com.edu.talim.dto.KochirishKursantDTO;
import com.edu.talim.dto.KochirishRequestDTO;
import com.edu.talim.dto.KochirishTarixiDTO;
import com.edu.talim.entity.Course;
import com.edu.talim.entity.Group;
import com.edu.talim.entity.KursKochirishTarixi;
import com.edu.talim.entity.OquvYili;
import com.edu.talim.entity.Student;
import com.edu.talim.entity.enums.KochirishTuri;
import com.edu.talim.entity.enums.TalabaHolati;
import com.edu.talim.repository.CourseRepository;
import com.edu.talim.repository.GroupRepository;
import com.edu.talim.repository.KursKochirishTarixiRepository;
import com.edu.talim.repository.OquvYiliRepository;
import com.edu.talim.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KursdanKursgaKochirishService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final OquvYiliRepository oquvYiliRepository;
    private final KursKochirishTarixiRepository tarixRepository;
    private final GroupRepository groupRepository;

    // Berilgan kurs (va ixtiyoriy guruh) bo'yicha FAOL kursantlar ro'yxati,
    // har biriga shu o'quv yilida allaqachon ko'chirilgan-ko'chirilmaganligi (holati) qo'shib beriladi
    public List<KochirishKursantDTO> getKursantlar(UUID kursId, UUID guruhId, Long oquvYiliId) {
        courseRepository.findById(kursId)
                .orElseThrow(() -> new NotFoundException("Kurs topilmadi: " + kursId));

        List<Student> kursantlar = studentRepository.findKochirishUchunKursantlar(kursId, guruhId);

        List<UUID> studentIds = kursantlar.stream().map(Student::getId).collect(Collectors.toList());
        Set<UUID> kochirilganlar = (oquvYiliId == null || studentIds.isEmpty())
                ? Set.of()
                : tarixRepository.findKochirilganStudentIdlar(studentIds, oquvYiliId, KochirishTuri.KOCHIRISH);

        return kursantlar.stream()
                .map(s -> KochirishKursantDTO.builder()
                        .studentId(s.getId())
                        .fio(s.getFio())
                        .joriyKursNomi(s.getCourse().getKursRaqami() + "-kurs")
                        .guruhNomi(s.getGroup() != null ? s.getGroup().getGuruhNomi() : null)
                        .holati(kochirilganlar.contains(s.getId()) ? "RUXSAT" : "TASDIQLASH")
                        .build())
                .collect(Collectors.toList());
    }

    // Tanlangan kursantlarni bir kursdan keyingisiga o'tkazish. Kursant o'zi biriktirilgan
    // GURUHI bilan birga ko'chadi - shuning uchun guruhning course FK si ham (bir marta,
    // dedupe qilingan holda) yangilanadi. Har bir hodisa uchun tarix yozuvi yaratiladi -
    // eski o'quv yilidagi jurnal/davomat/baho ma'lumotlari (allaqachon oquvYili ga bog'langani
    // uchun) hech qanday o'zgarishsiz saqlanib qoladi.
    @Transactional
    public void kochirish(KochirishRequestDTO dto) {
        if (dto.getStudentIds() == null || dto.getStudentIds().isEmpty()) {
            throw new RuntimeException("Kamida bitta kursant tanlang!");
        }

        OquvYili oquvYili = oquvYiliRepository.findById(dto.getOquvYiliId())
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi"));

        // Ko'chirilayotgan guruhlarni bir marta yig'amiz (dedupe) - har biri faqat
        // BITTA marta yangilanadi, hatto guruhda bir nechta kursant tanlangan bo'lsa ham
        Map<UUID, Group> tegishliGuruhlar = new LinkedHashMap<>();
        Map<UUID, Course> guruhningYangiKursi = new LinkedHashMap<>();

        for (UUID studentId : dto.getStudentIds()) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Kursant topilmadi: " + studentId));

            if (student.getHolati() != TalabaHolati.FAOL) {
                throw new ConflictException(
                        "Kursant " + student.getFio() + " faol emas (chetlatilgan/bitirgan), ko'chirib bo'lmaydi!");
            }

            Course eskiKurs = student.getCourse();
            if (eskiKurs == null) {
                throw new RuntimeException("Kursant " + student.getFio() + " biror kursga biriktirilmagan!");
            }

            if (eskiKurs.getKursRaqami() >= 4) {
                throw new ConflictException(
                        "Kursant " + student.getFio() + " 4-kursda - keyingi kurs mavjud emas! "
                                + "\"Arxivlash\" funksiyasidan foydalaning.");
            }

            // Shu o'quv yilida allaqachon ko'chirilgan bo'lsa - qayta ishlanmaydi (idempotent)
            boolean allaqachonKochirilgan = tarixRepository.existsByStudentIdAndOquvYiliIdAndTuri(
                    studentId, oquvYili.getId(), KochirishTuri.KOCHIRISH);
            if (allaqachonKochirilgan) {
                continue;
            }

            Course yangiKurs = courseRepository.findByKursRaqami(eskiKurs.getKursRaqami() + 1)
                    .orElseThrow(() -> new NotFoundException(
                            (eskiKurs.getKursRaqami() + 1) + "-kurs tizimda mavjud emas! Avval uni yarating."));

            tarixRepository.save(KursKochirishTarixi.builder()
                    .student(student)
                    .turi(KochirishTuri.KOCHIRISH)
                    .eskiKurs(eskiKurs)
                    .yangiKurs(yangiKurs)
                    .oquvYili(oquvYili)
                    .sana(LocalDate.now())
                    .build());

            // Kursanti kursi yangilanadi (guruhi - Student.group - o'zgarmaydi)
            student.setCourse(yangiKurs);
            studentRepository.save(student);

            // Shu kursantning guruhini "yangilanishi kerak bo'lganlar" ro'yxatiga qo'shamiz
            Group guruh = student.getGroup();
            if (guruh != null) {
                Course avvalgiRejalashtirilgan = guruhningYangiKursi.get(guruh.getId());
                if (avvalgiRejalashtirilgan != null && !avvalgiRejalashtirilgan.getId().equals(yangiKurs.getId())) {
                    // Bir xil guruhdagi kursantlar turli kurslarga tanlangan - ma'lumot nomuvofiqligi
                    throw new ConflictException(
                            "\"" + guruh.getGuruhNomi() + "\" guruhidagi kursantlar turli kurslardan tanlangan, "
                                    + "ko'chirish mumkin emas! Bitta guruhning barcha kursantlari bitta kursda bo'lishi kerak.");
                }
                guruhningYangiKursi.put(guruh.getId(), yangiKurs);
                tegishliGuruhlar.put(guruh.getId(), guruh);
            }
        }

        // Endi tegishli guruhlarning o'zini ham yangi kursga o'tkazamiz (har biri FAQAT bir marta)
        for (Map.Entry<UUID, Group> entry : tegishliGuruhlar.entrySet()) {
            Group guruh = entry.getValue();
            Course yangiKurs = guruhningYangiKursi.get(entry.getKey());
            guruh.setCourse(yangiKurs);
            groupRepository.save(guruh);
        }
    }

    // O'qishdan doimiy chetlatish. Kursant "kursantlar safi"dan (standart ro'yxatdan)
    // chiqib ketadi (StudentRepository.findAllWithFilters holati=FAOL filtri orqali),
    // lekin yozuvi bazada butunligicha saqlanib qoladi - hisob yuritish uchun.
    @Transactional
    public void chetlatish(ChetlatishRequestDTO dto) {
        if (dto.getStudentIds() == null || dto.getStudentIds().isEmpty()) {
            throw new RuntimeException("Kamida bitta kursant tanlang!");
        }
        if (dto.getSababi() == null || dto.getSababi().isBlank()) {
            throw new RuntimeException("Chetlatish sababini kiriting!");
        }

        OquvYili oquvYili = oquvYiliRepository.findById(dto.getOquvYiliId())
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi"));

        for (UUID studentId : dto.getStudentIds()) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Kursant topilmadi: " + studentId));

            if (student.getHolati() != TalabaHolati.FAOL) {
                continue; // allaqachon chetlatilgan/bitirgan
            }

            tarixRepository.save(KursKochirishTarixi.builder()
                    .student(student)
                    .turi(KochirishTuri.CHETLATISH)
                    .eskiKurs(student.getCourse())
                    .yangiKurs(null)
                    .oquvYili(oquvYili)
                    .sababi(dto.getSababi())
                    .sana(LocalDate.now())
                    .build());

            student.setHolati(TalabaHolati.CHETLATILGAN);
            studentRepository.save(student);
        }
    }

    // 4-kurs bitiruvchilarini arxivlash - ular ham standart ro'yxatdan chiqadi,
    // lekin ma'lumotlari butunligicha saqlanadi.
    @Transactional
    public void arxivlash(ArxivlashRequestDTO dto) {
        if (dto.getStudentIds() == null || dto.getStudentIds().isEmpty()) {
            throw new RuntimeException("Kamida bitta kursant tanlang!");
        }

        OquvYili oquvYili = oquvYiliRepository.findById(dto.getOquvYiliId())
                .orElseThrow(() -> new NotFoundException("O'quv yili topilmadi"));

        for (UUID studentId : dto.getStudentIds()) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Kursant topilmadi: " + studentId));

            if (student.getHolati() != TalabaHolati.FAOL) {
                continue;
            }

            Course joriyKurs = student.getCourse();
            if (joriyKurs == null || joriyKurs.getKursRaqami() < 4) {
                throw new ConflictException(
                        "Faqat 4-kurs kursantlarini arxivlash mumkin! (" + student.getFio() + ")");
            }

            tarixRepository.save(KursKochirishTarixi.builder()
                    .student(student)
                    .turi(KochirishTuri.ARXIVLASH)
                    .eskiKurs(joriyKurs)
                    .yangiKurs(null)
                    .oquvYili(oquvYili)
                    .sana(LocalDate.now())
                    .build());

            student.setHolati(TalabaHolati.BITIRGAN);
            studentRepository.save(student);
        }
    }

    // Bitta kursantning butun kurs-o'zgarish tarixi (eng yangisidan boshlab)
    public List<KochirishTarixiDTO> getTarix(UUID studentId) {
        return tarixRepository.findByStudentIdOrderBySanaDesc(studentId)
                .stream()
                .map(t -> KochirishTarixiDTO.builder()
                        .id(t.getId())
                        .turi(t.getTuri())
                        .eskiKursNomi(t.getEskiKurs() != null ? t.getEskiKurs().getKursRaqami() + "-kurs" : null)
                        .yangiKursNomi(t.getYangiKurs() != null ? t.getYangiKurs().getKursRaqami() + "-kurs" : null)
                        .oquvYiliNomi(t.getOquvYili() != null ? t.getOquvYili().getNom() : null)
                        .sababi(t.getSababi())
                        .sana(t.getSana())
                        .build())
                .collect(Collectors.toList());
    }
}