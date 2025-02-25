package org.main.unimapapi.controllers;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SubjectController {

    @PostMapping("resources/subjects")
    public void getAllSubjects() {

    }
}
