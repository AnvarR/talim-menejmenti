package com.edu.talim.service;

import com.edu.talim.entity.Course;
import com.edu.talim.entity.Group;
import com.edu.talim.repository.CourseRepository;
import com.edu.talim.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;

    public List<Group> getAll() {
        return groupRepository.findAll();
    }

    public List<Group> getByCourseId(Long courseId) {
        return groupRepository.findByCourseId(courseId);
    }

    public Group create(String guruhNomi, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Kurs topilmadi"));
        Group group = Group.builder()
                .guruhNomi(guruhNomi)
                .course(course)
                .build();
        return groupRepository.save(group);
    }
}