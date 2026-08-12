package com.edu.talim.service;

import com.edu.talim.exception.ConflictException;

import com.edu.talim.exception.NotFoundException;

import com.edu.talim.dto.GroupResponseDTO;
import com.edu.talim.dto.StudentListDTO;
import com.edu.talim.entity.Course;
import com.edu.talim.entity.Group;
import com.edu.talim.entity.Student;
import com.edu.talim.repository.CourseRepository;
import com.edu.talim.repository.FanTaqsimlashRepository;
import com.edu.talim.repository.GroupRepository;
import com.edu.talim.repository.OqituvchiFanTaqsimlashRepository;
import com.edu.talim.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final FanTaqsimlashRepository fanTaqsimlashRepository;
    private final OqituvchiFanTaqsimlashRepository oqituvchiFanTaqsimlashRepository;

    // Barcha guruhlar (tinglovchi uchun ishlatiladi)
    public List<Group> getAll() {
        return groupRepository.findAll();
    }

    // Kurs bo'yicha guruhlar
    public List<Group> getByCourseId(Long courseId) {
        return groupRepository.findByCourseId(courseId);
    }

    // Faqat kursant guruhlari ro'yxati (fakultet boshlig'i uchun)
    public List<GroupResponseDTO> getKursantGuruhlari() {
        return groupRepository.findKursantGuruhlari()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Yangi guruh yaratish
    @Transactional
    public GroupResponseDTO create(String guruhNomi, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Kurs topilmadi: " + courseId));

        if (groupRepository.findByGuruhNomiAndCourseId(guruhNomi, courseId).isPresent()) {
            throw new ConflictException("Bu guruh allaqachon mavjud: " + guruhNomi);
        }

        Group group = Group.builder()
                .guruhNomi(guruhNomi)
                .course(course)
                .build();

        return toDTO(groupRepository.save(group));
    }

    // Guruh nomini tahrirlash
    @Transactional
    public GroupResponseDTO update(Long id, String yangiNom) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Guruh topilmadi: " + id));

        group.setGuruhNomi(yangiNom);
        return toDTO(groupRepository.save(group));
    }

    // Guruhni o'chirish
    @Transactional
    public void delete(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Guruh topilmadi: " + id));

        long talabaSoni = groupRepository.countBarchaTalabalarByGuruhId(id);
        if (talabaSoni > 0) {
            throw new RuntimeException(
                    "Guruhda " + talabaSoni + " ta talaba (kursant/tinglovchi) bor, avval ularni chiqaring!");
        }

        long fanTaqsimlashSoni = fanTaqsimlashRepository.countByGuruhId(id);
        if (fanTaqsimlashSoni > 0) {
            throw new RuntimeException(
                    "Bu guruhga " + fanTaqsimlashSoni + " ta fan taqsimlangan! "
                            + "Avval \"Fanlarni taqsimlash\" bo'limida shu guruhga tegishli taqsimlashlarni o'chiring.");
        }

        if (oqituvchiFanTaqsimlashRepository.existsByGuruhId(id)) {
            throw new RuntimeException(
                    "Bu guruh biror o'qituvchiga (fan taqsimlash) biriktirilgan! "
                            + "Avval o'sha taqsimlashni o'chiring yoki guruhni undan chiqaring.");
        }

        groupRepository.deleteById(id);
    }

    // Guruhga biriktirilmagan kursantlar (kurs bo'yicha)
    public List<StudentListDTO> getBiriktirilmaganKursantlar(Long kursId) {
        return groupRepository.findBiriktirilmaganKursantlar(kursId)
                .stream()
                .map(this::toStudentListDTO)
                .collect(Collectors.toList());
    }

    // Kursantni guruhga biriktirish
    @Transactional
    public void kursantBiriktirish(Long guruhId, Long studentId) {
        Group group = groupRepository.findById(guruhId)
                .orElseThrow(() -> new NotFoundException("Guruh topilmadi: " + guruhId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Kursant topilmadi: " + studentId));

        if (student.getGroup() != null) {
            throw new ConflictException(
                    "Kursant allaqachon " + student.getGroup().getGuruhNomi() + " guruhida!");
        }

        student.setGroup(group);
        studentRepository.save(student);
    }

    // Kursantni guruhdan chiqarish
    @Transactional
    public void kursantChiqarish(Long guruhId, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Kursant topilmadi: " + studentId));

        if (student.getGroup() == null || !student.getGroup().getId().equals(guruhId)) {
            throw new RuntimeException("Kursant bu guruhda emas!");
        }

        student.setGroup(null);
        studentRepository.save(student);
    }

    // ===== HELPER =====

    private GroupResponseDTO toDTO(Group group) {
        long kursantSoni = groupRepository.countKursantlarByGuruhId(group.getId());
        return GroupResponseDTO.builder()
                .id(group.getId())
                .guruhNomi(group.getGuruhNomi())
                .kursId(group.getCourse() != null ? group.getCourse().getId() : null)
                .kursNomi(group.getCourse() != null ? group.getCourse().getKursRaqami() + "-kurs" : null)
                .kursantSoni(kursantSoni)
                .build();
    }

    private StudentListDTO toStudentListDTO(Student s) {
        return StudentListDTO.builder()
                .id(s.getId())
                .fio(s.getFio())
                .kursi(s.getCourse() != null ? s.getCourse().getKursRaqami() + "-kurs" : null)
                .guruhi(s.getGroup() != null ? s.getGroup().getGuruhNomi() : null)
                .jinsi(s.getJinsi() != null ? s.getJinsi().getLabel() : null)
                .type(s.getType().name())
                .build();
    }
}