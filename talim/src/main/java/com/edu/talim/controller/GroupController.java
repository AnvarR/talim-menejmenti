package com.edu.talim.controller;

import com.edu.talim.entity.Group;
import com.edu.talim.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<List<Group>> getAll() {
        return ResponseEntity.ok(groupService.getAll());
    }

    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<Group>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(groupService.getByCourseId(courseId));
    }

    @PostMapping
    public ResponseEntity<Group> create(
            @RequestParam String guruhNomi,
            @RequestParam Long courseId
    ) {
        return ResponseEntity.ok(groupService.create(guruhNomi, courseId));
    }
}