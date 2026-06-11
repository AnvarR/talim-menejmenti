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

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("""
        SELECT s FROM Student s
        LEFT JOIN s.course c
        LEFT JOIN s.group g
        WHERE s.type = :type
        AND (:oquvYili IS NULL OR c.oquvYili = :oquvYili)
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

    Optional<Student> findByUsername(String username);
}