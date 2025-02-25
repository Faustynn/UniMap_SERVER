package org.main.unimapapi.controllers;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TeacherController {

    @PostMapping("resources/teachers")
    public void getAllSubjects() {

    }
}
