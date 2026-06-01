package com.edu.talim.service;

import com.edu.talim.entity.Course;
import com.edu.talim.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    public Course create(Integer kursRaqami, String oquvYili) {
        Course course = Course.builder()
                .kursRaqami(kursRaqami)
                .oquvYili(oquvYili)
                .build();
        return courseRepository.save(course);
    }
}