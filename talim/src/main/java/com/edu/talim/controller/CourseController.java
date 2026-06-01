package com.edu.talim.controller;

import com.edu.talim.entity.Course;
import com.edu.talim.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<Course>> getAll() {
        return ResponseEntity.ok(courseService.getAll());
    }

    @PostMapping
    public ResponseEntity<Course> create(@RequestBody Map<String, Object> body) {
        Integer kursRaqami = (Integer) body.get("kursRaqami");
        String oquvYili = (String) body.get("oquvYili");
        return ResponseEntity.ok(courseService.create(kursRaqami, oquvYili));
    }
}