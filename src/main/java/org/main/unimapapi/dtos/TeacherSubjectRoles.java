package org.main.unimapapi.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO representing a subject and the roles a teacher holds in that subject.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSubjectRoles {

    /**
     * The name of the subject the teacher is associated with.
     */
    private String subjectName;

    /**
     * List of roles the teacher has in the subject (e.g., {@code ["lecturer", "guarantor"]}).
     */
    private List<String> roles;
}