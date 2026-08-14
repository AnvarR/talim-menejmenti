package com.edu.talim.repository;

import com.edu.talim.entity.Student;
import com.edu.talim.entity.enums.Jins;
import com.edu.talim.entity.enums.StudentType;
import com.edu.talim.entity.enums.TalabaHolati;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Standart ro'yxat - faqat FAOL kursantlar (chetlatilgan/bitirganlar avtomatik chiqarib tashlanadi)
    @Query("""
        SELECT s FROM Student s
        LEFT JOIN s.course c
        LEFT JOIN s.group g
        LEFT JOIN s.oquvYili oy
        WHERE s.type = :type
        AND s.holati = com.edu.talim.entity.enums.TalabaHolati.FAOL
        AND (:oquvYili IS NULL OR oy.nom = :oquvYili)
        AND (:kurs IS NULL OR c.kursRaqami = :kurs)
        AND (:guruh IS NULL OR g.guruhNomi = :guruh)
        AND (:jinsi IS NULL OR s.jinsi = :jinsi)
    """)
    Page<Student> findAllWithFilters(
            @Param("type") StudentType type,
            @Param("oquvYili") String oquvYili,
            @Param("kurs") Integer kurs,
            @Param("guruh") String guruh,
            @Param("jinsi") Jins jinsi,
            Pageable pageable
    );

    boolean existsByJshshir(String jshshir);

    boolean existsByReytingDaftarchasiRaqami(String reytingDaftarchasiRaqami);

    Optional<Student> findByUsername(String username);

    // Guruhga tegishli kursantlar ro'yxati
    List<Student> findByGroupId(Long groupId);

    // Guruhga tegishli kursantlar - fio bo'yicha alifbo tartibida
    List<Student> findByGroupIdOrderByFioAsc(Long groupId);

    // Kursdan-kursga ko'chirish sahifasi uchun: shu kurs/guruhdagi FAOL kursantlar
    @Query("""
        SELECT s FROM Student s
        LEFT JOIN s.group g
        WHERE s.type = 'KURSANT'
        AND s.holati = com.edu.talim.entity.enums.TalabaHolati.FAOL
        AND s.course.id = :kursId
        AND (:guruhId IS NULL OR g.id = :guruhId)
        ORDER BY s.fio ASC
    """)
    List<Student> findKochirishUchunKursantlar(@Param("kursId") Long kursId,
                                               @Param("guruhId") Long guruhId);
}