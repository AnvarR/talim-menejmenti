package com.edu.talim.repository;

import com.edu.talim.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByOquvYili(String oquvYili);
    Optional<Course> findByKursRaqami(Integer kursRaqami);
}
