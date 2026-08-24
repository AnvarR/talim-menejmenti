package com.edu.talim.repository;

import com.edu.talim.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

    List<Group> findByCourseId(UUID courseId);

    Optional<Group> findByGuruhNomiAndCourseId(String guruhNomi, UUID courseId);

    Optional<Group> findByGuruhNomi(String guruhNomi);

    // Faqat kursant guruhlari (KURSANT tipidagi studentlar bog'langan guruhlar)
    @Query("SELECT DISTINCT g FROM Group g JOIN Student s ON s.group.id = g.id WHERE s.type = 'KURSANT'")
    List<Group> findKursantGuruhlari();

    // Kursantlar soni (guruh bo'yicha, faqat KURSANT turi - ro'yxatda ko'rsatish uchun)
    @Query("SELECT COUNT(s) FROM Student s WHERE s.group.id = :guruhId AND s.type = 'KURSANT'")
    Long countKursantlarByGuruhId(UUID guruhId);

    // Guruhga bog'langan BARCHA talabalar soni (KURSANT va TINGLOVCHI birgalikda) -
    // guruhni o'chirishdan oldin xavfsiz tekshirish uchun ishlatiladi
    @Query("SELECT COUNT(s) FROM Student s WHERE s.group.id = :guruhId")
    Long countBarchaTalabalarByGuruhId(UUID guruhId);

    // Guruhga biriktirilmagan kursantlar (kurs bo'yicha)
    @Query("SELECT s FROM Student s WHERE s.type = 'KURSANT' AND s.course.id = :kursId AND s.group IS NULL")
    List<com.edu.talim.entity.Student> findBiriktirilmaganKursantlar(UUID kursId);
}