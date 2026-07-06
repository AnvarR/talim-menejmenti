package com.edu.talim.service;

import com.edu.talim.dto.StudentCreateDTO;
import com.edu.talim.dto.StudentDetailDTO;
import com.edu.talim.dto.StudentListDTO;
import com.edu.talim.entity.Course;
import com.edu.talim.entity.Group;
import com.edu.talim.entity.Student;
import com.edu.talim.entity.enums.*;
import com.edu.talim.repository.CourseRepository;
import com.edu.talim.repository.GroupRepository;
import com.edu.talim.repository.InstitutdanChiqishRepository;
import com.edu.talim.repository.StudentRepository;
import com.edu.talim.repository.SutkalikNaryadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;
    private final FileService fileService;
    private final InstitutdanChiqishRepository institutdanChiqishRepository;
    private final SutkalikNaryadRepository sutkalikNaryadRepository;

    // Ro'yxat (filter bilan)
    public Page<StudentListDTO> getAll(
            String type,
            String oquvYili,
            Integer kurs,
            String guruh,
            String fio,
            String jinsi,
            int page,
            int size
    ) {
        StudentType studentType = StudentType.valueOf(type.toUpperCase());
        Jins jinsEnum = (jinsi != null && !jinsi.isEmpty())
                ? Jins.fromLabel(jinsi) : null;

        Pageable pageable = PageRequest.of(page, size);

        return studentRepository
                .findAllWithFilters(studentType, oquvYili, kurs, guruh, jinsEnum, pageable)
                .map(this::toListDTO);
    }

    // Bitta student
    public StudentDetailDTO getById(Long id) {
        return toDetailDTO(findById(id));
    }

    // Qo'shish
    public StudentDetailDTO create(StudentCreateDTO dto) {
        if (studentRepository.existsByJshshir(dto.getJshshir())) {
            throw new RuntimeException("Bu JSHSHIR bilan student allaqachon mavjud!");
        }
        Student student = buildStudent(dto);
        return toDetailDTO(studentRepository.save(student));
    }

    // Rasm yuklash
    public String uploadPhoto(Long id, MultipartFile file) {
        Student student = findById(id);
        if (student.getPhotoUrl() != null) {
            fileService.deleteFile(student.getPhotoUrl());
        }
        String photoUrl = fileService.saveFile(file);
        student.setPhotoUrl(photoUrl);
        studentRepository.save(student);
        return photoUrl;
    }

    // Tahrirlash
    public StudentDetailDTO update(Long id, StudentCreateDTO dto) {
        Student student = findById(id);
        updateStudent(student, dto);
        return toDetailDTO(studentRepository.save(student));
    }

    // O'chirish
    public void delete(Long id) {
        Student student = findById(id);

        // Institutdan chiqish yozuvi bormi tekshirish
        if (institutdanChiqishRepository.existsByStudentId(id)) {
            throw new RuntimeException(
                    "Bu kursantda institutdan chiqish yozuvi mavjud, o'chirib bo'lmaydi!");
        }

        // Sutkalik naryad yozuvi bormi tekshirish
        if (sutkalikNaryadRepository.existsByStudentId(id)) {
            throw new RuntimeException(
                    "Bu kursantda sutkalik naryad yozuvi mavjud, o'chirib bo'lmaydi!");
        }

        if (student.getPhotoUrl() != null) {
            fileService.deleteFile(student.getPhotoUrl());
        }
        studentRepository.delete(student);
    }

    // ===== HELPER METODLAR =====

    private Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student topilmadi: " + id));
    }

    // "12.01.2024" yoki "2024-01-12" → LocalDate
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

    // "3-kurs" → 3
    private Integer parseKurs(String kursi) {
        if (kursi == null || kursi.isEmpty()) return null;
        try {
            return Integer.parseInt(kursi.replace("-kurs", "").trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Student buildStudent(StudentCreateDTO dto) {
        // Kurs topish
        Course course = null;
        if (dto.getKursi() != null && !dto.getKursi().isEmpty()) {
            course = courseRepository
                    .findByKursRaqami(parseKurs(dto.getKursi()))
                    .orElseThrow(() -> new RuntimeException("Kurs topilmadi"));
        }

        // Guruh faqat TINGLOVCHI uchun
        Group group = null;
        boolean tinglovchiMi = "TINGLOVCHI".equalsIgnoreCase(dto.getType());
        if (tinglovchiMi && dto.getGuruhi() != null && !dto.getGuruhi().isEmpty()) {
            if (course != null) {
                Course finalCourse = course;
                group = groupRepository
                        .findByGuruhNomiAndCourseId(dto.getGuruhi(), course.getId())
                        .orElseGet(() -> groupRepository.save(
                                Group.builder()
                                        .guruhNomi(dto.getGuruhi())
                                        .course(finalCourse)
                                        .build()
                        ));
            } else {
                group = groupRepository
                        .findByGuruhNomi(dto.getGuruhi())
                        .orElseGet(() -> groupRepository.save(
                                Group.builder()
                                        .guruhNomi(dto.getGuruhi())
                                        .course(null)
                                        .build()
                        ));
            }
        }

        // Avtomatik username, password, role
        String username = dto.getPassportSeria();
        String password = "12345678";
        Role role = tinglovchiMi ? Role.TINGLOVCHI : Role.KURSANT;

        return Student.builder()
                .jshshir(dto.getJshshir())
                .fio(dto.getFio())
                .malumoti(dto.getMalumoti() != null ? Malumot.fromLabel(dto.getMalumoti()) : null)
                .passportSeria(dto.getPassportSeria())
                .hujjatBerilganSana(parseDate(dto.getHujjatBerilganSana()))
                .jinsi(Jins.fromLabel(dto.getJinsi()))
                .tugilganSana(parseDate(dto.getTugilganSana()))
                .millati(Millat.fromLabel(dto.getMillati()))
                .hujjatBerganTashkilot(dto.getHujjatBerganTashkilot())
                .fuqaroligi(Fuqarolik.fromLabel(dto.getFuqaroligi()))
                .telefon1(dto.getTelefon1())
                .telefon2(dto.getTelefon2())
                .email1(dto.getEmail1())
                .email2(dto.getEmail2())
                .harbiyUnvoni(dto.getHarbiyUnvoni())
                .guvohnomaNomeri(dto.getGuvohnomaNomeri())
                .course(course)
                .group(group)
                .lavozimi(dto.getLavozimi())
                .type(StudentType.fromLabel(dto.getType()))
                .username(username)
                .password(password)
                .role(role)
                .build();
    }

    private void updateStudent(Student student, StudentCreateDTO dto) {
        boolean tinglovchiMi = "TINGLOVCHI".equalsIgnoreCase(dto.getType());

        student.setFio(dto.getFio());
        student.setJshshir(dto.getJshshir());
        student.setMalumoti(dto.getMalumoti() != null ? Malumot.fromLabel(dto.getMalumoti()) : null);
        student.setPassportSeria(dto.getPassportSeria());
        student.setHujjatBerilganSana(parseDate(dto.getHujjatBerilganSana()));
        student.setJinsi(Jins.fromLabel(dto.getJinsi()));
        student.setTugilganSana(parseDate(dto.getTugilganSana()));
        student.setMillati(Millat.fromLabel(dto.getMillati()));
        student.setHujjatBerganTashkilot(dto.getHujjatBerganTashkilot());
        student.setFuqaroligi(Fuqarolik.fromLabel(dto.getFuqaroligi()));
        student.setTelefon1(dto.getTelefon1());
        student.setTelefon2(dto.getTelefon2());
        student.setEmail1(dto.getEmail1());
        student.setEmail2(dto.getEmail2());
        student.setHarbiyUnvoni(dto.getHarbiyUnvoni());
        student.setGuvohnomaNomeri(dto.getGuvohnomaNomeri());
        student.setLavozimi(dto.getLavozimi());
        // Username ni passportSeria bilan yangilash
        student.setUsername(dto.getPassportSeria());

        if (dto.getKursi() != null && !dto.getKursi().isEmpty()) {
            Course course = courseRepository
                    .findByKursRaqami(parseKurs(dto.getKursi()))
                    .orElseThrow(() -> new RuntimeException("Kurs topilmadi"));
            student.setCourse(course);

            // Guruh faqat TINGLOVCHI uchun yangilanadi
            if (tinglovchiMi && dto.getGuruhi() != null && !dto.getGuruhi().isEmpty()) {
                Course finalCourse = course;
                Group group = groupRepository
                        .findByGuruhNomiAndCourseId(dto.getGuruhi(), course.getId())
                        .orElseGet(() -> groupRepository.save(
                                Group.builder()
                                        .guruhNomi(dto.getGuruhi())
                                        .course(finalCourse)
                                        .build()
                        ));
                student.setGroup(group);
            }
        } else {
            student.setCourse(null);
            if (tinglovchiMi && dto.getGuruhi() != null && !dto.getGuruhi().isEmpty()) {
                Group group = groupRepository
                        .findByGuruhNomi(dto.getGuruhi())
                        .orElseGet(() -> groupRepository.save(
                                Group.builder()
                                        .guruhNomi(dto.getGuruhi())
                                        .course(null)
                                        .build()
                        ));
                student.setGroup(group);
            }
        }
    }

    private StudentListDTO toListDTO(Student s) {
        return StudentListDTO.builder()
                .id(s.getId())
                .oquvYili(s.getCourse() != null ? s.getCourse().getOquvYili() : null)
                .kursi(s.getCourse() != null ? s.getCourse().getKursRaqami() + "-kurs" : null)
                .guruhi(s.getGroup() != null ? s.getGroup().getGuruhNomi() : null)
                .fio(s.getFio())
                .jinsi(s.getJinsi() != null ? s.getJinsi().getLabel() : null)
                .type(s.getType().name())
                .build();
    }

    private StudentDetailDTO toDetailDTO(Student s) {
        return StudentDetailDTO.builder()
                .id(s.getId())
                .photoUrl(s.getPhotoUrl())
                .jshshir(s.getJshshir())
                .fio(s.getFio())
                .malumoti(s.getMalumoti() != null ? s.getMalumoti().getLabel() : null)
                .passportSeria(s.getPassportSeria())
                .hujjatBerilganSana(s.getHujjatBerilganSana())
                .jinsi(s.getJinsi() != null ? s.getJinsi().getLabel() : null)
                .tugilganSana(s.getTugilganSana())
                .millati(s.getMillati() != null ? s.getMillati().getLabel() : null)
                .hujjatBerganTashkilot(s.getHujjatBerganTashkilot())
                .fuqaroligi(s.getFuqaroligi() != null ? s.getFuqaroligi().getLabel() : null)
                .telefon1(s.getTelefon1())
                .telefon2(s.getTelefon2())
                .email1(s.getEmail1())
                .email2(s.getEmail2())
                .harbiyUnvoni(s.getHarbiyUnvoni())
                .guvohnomaNomeri(s.getGuvohnomaNomeri())
                .kursi(s.getCourse() != null ? s.getCourse().getKursRaqami() + "-kurs" : null)
                .guruhi(s.getGroup() != null ? s.getGroup().getGuruhNomi() : null)
                .lavozimi(s.getLavozimi())
                .type(s.getType().name())
                .createdAt(s.getCreatedAt())
                .build();
    }
}