package com.edu.talim.repository;

import com.edu.talim.entity.Student;
import com.edu.talim.entity.enums.Jins;
import com.edu.talim.entity.enums.StudentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("""
        SELECT s FROM Student s
        WHERE s.type = :type
        AND (:oquvYili IS NULL OR s.course.oquvYili = :oquvYili)
        AND (:kurs IS NULL OR s.course.kursRaqami = :kurs)
        AND (:guruh IS NULL OR s.group.guruhNomi = :guruh)
        AND (:fio IS NULL OR s.fio = :fio)
        AND (:jinsi IS NULL OR s.jinsi = :jinsi)
    """)
    Page<Student> findAllWithFilters(
            @Param("type") StudentType type,
            @Param("oquvYili") String oquvYili,
            @Param("kurs") Integer kurs,
            @Param("guruh") String guruh,
            @Param("fio") String fio,
            @Param("jinsi") Jins jinsi,
            Pageable pageable
    );

    boolean existsByJshshir(String jshshir);
}