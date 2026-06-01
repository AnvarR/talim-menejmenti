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
import com.edu.talim.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;
    private final FileService fileService;

    // Ro'yxat
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
                ? Jins.valueOf(jinsi.toUpperCase()) : null;

        Pageable pageable = PageRequest.of(page, size);

        return studentRepository
                .findAllWithFilters(studentType, oquvYili, kurs, guruh, fio, jinsEnum, pageable)
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

    private Student buildStudent(StudentCreateDTO dto) {
        Course course = null;
        if (dto.getCourseId() != null) {
            course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Kurs topilmadi"));
        }
        Group group = null;
        if (dto.getGroupId() != null) {
            group = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Guruh topilmadi"));
        }
        return Student.builder()
                .jshshir(dto.getJshshir())
                .fio(dto.getFio())
                .malumoti(dto.getMalumoti() != null ? Malumot.valueOf(dto.getMalumoti().toUpperCase()) : null)
                .passportSeria(dto.getPassportSeria())
                .hujjatBerilganSana(dto.getHujjatBerilganSana())
                .jinsi(Jins.valueOf(dto.getJinsi().toUpperCase()))
                .tugilganSana(dto.getTugilganSana())
                .millati(Millat.valueOf(dto.getMillati().toUpperCase()))
                .hujjatBerganTashkilot(dto.getHujjatBerganTashkilot())
                .fuqaroligi(Fuqarolik.valueOf(dto.getFuqaroligi().toUpperCase()))
                .telefon1(dto.getTelefon1())
                .telefon2(dto.getTelefon2())
                .email1(dto.getEmail1())
                .email2(dto.getEmail2())
                .harbiyUnvoni(dto.getHarbiyUnvoni() != null ? HarbiyUnvon.valueOf(dto.getHarbiyUnvoni().toUpperCase()) : null)
                .guvohnomaNomeri(dto.getGuvohnomaNomeri())
                .course(course)
                .group(group)
                .lavozimi(dto.getLavozimi())
                .type(StudentType.valueOf(dto.getType().toUpperCase()))
                .build();
    }

    private void updateStudent(Student student, StudentCreateDTO dto) {
        student.setFio(dto.getFio());
        student.setJshshir(dto.getJshshir());
        student.setMalumoti(dto.getMalumoti() != null ? Malumot.valueOf(dto.getMalumoti().toUpperCase()) : null);
        student.setPassportSeria(dto.getPassportSeria());
        student.setHujjatBerilganSana(dto.getHujjatBerilganSana());
        student.setJinsi(Jins.valueOf(dto.getJinsi().toUpperCase()));
        student.setTugilganSana(dto.getTugilganSana());
        student.setMillati(Millat.valueOf(dto.getMillati().toUpperCase()));
        student.setHujjatBerganTashkilot(dto.getHujjatBerganTashkilot());
        student.setFuqaroligi(Fuqarolik.valueOf(dto.getFuqaroligi().toUpperCase()));
        student.setTelefon1(dto.getTelefon1());
        student.setTelefon2(dto.getTelefon2());
        student.setEmail1(dto.getEmail1());
        student.setEmail2(dto.getEmail2());
        student.setHarbiyUnvoni(dto.getHarbiyUnvoni() != null ? HarbiyUnvon.valueOf(dto.getHarbiyUnvoni().toUpperCase()) : null);
        student.setGuvohnomaNomeri(dto.getGuvohnomaNomeri());
        student.setLavozimi(dto.getLavozimi());
        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Kurs topilmadi"));
            student.setCourse(course);
        }
        if (dto.getGroupId() != null) {
            Group group = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Guruh topilmadi"));
            student.setGroup(group);
        }
    }

    private StudentListDTO toListDTO(Student s) {
        return StudentListDTO.builder()
                .id(s.getId())
                .oquvYili(s.getCourse() != null ? s.getCourse().getOquvYili() : null)
                .kursi(s.getCourse() != null ? s.getCourse().getKursRaqami() + "-kurs" : null)
                .guruhi(s.getGroup() != null ? s.getGroup().getGuruhNomi() : null)
                .fio(s.getFio())
                .jinsi(s.getJinsi() != null ? s.getJinsi().name() : null)
                .type(s.getType().name())
                .build();
    }

    private StudentDetailDTO toDetailDTO(Student s) {
        return StudentDetailDTO.builder()
                .id(s.getId())
                .photoUrl(s.getPhotoUrl())
                .jshshir(s.getJshshir())
                .fio(s.getFio())
                .malumoti(s.getMalumoti() != null ? s.getMalumoti().name() : null)
                .passportSeria(s.getPassportSeria())
                .hujjatBerilganSana(s.getHujjatBerilganSana())
                .jinsi(s.getJinsi() != null ? s.getJinsi().name() : null)
                .tugilganSana(s.getTugilganSana())
                .millati(s.getMillati() != null ? s.getMillati().name() : null)
                .hujjatBerganTashkilot(s.getHujjatBerganTashkilot())
                .fuqaroligi(s.getFuqaroligi() != null ? s.getFuqaroligi().name() : null)
                .telefon1(s.getTelefon1())
                .telefon2(s.getTelefon2())
                .email1(s.getEmail1())
                .email2(s.getEmail2())
                .harbiyUnvoni(s.getHarbiyUnvoni() != null ? s.getHarbiyUnvoni().name() : null)
                .guvohnomaNomeri(s.getGuvohnomaNomeri())
                .kursi(s.getCourse() != null ? s.getCourse().getKursRaqami() + "-kurs" : null)
                .guruhi(s.getGroup() != null ? s.getGroup().getGuruhNomi() : null)
                .lavozimi(s.getLavozimi())
                .type(s.getType().name())
                .createdAt(s.getCreatedAt())
                .build();
    }
}