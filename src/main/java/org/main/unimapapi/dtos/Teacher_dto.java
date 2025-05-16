package org.main.unimapapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing a teacher, including contact info and associated subjects.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher_dto {

    /**
     * Unique identifier of the teacher (e.g., LDAP ID or internal code).
     */
    private String id;

    /**
     * Full name of the teacher.
     */
    private String name;

    /**
     * Email address of the teacher.
     */
    private String email;

    /**
     * Phone number of the teacher (optional).
     */
    private String phone;

    /**
     * Office location of the teacher (e.g., {@code "BC-322"}).
     */
    private String office;

    /**
     * List of subjects the teacher is associated with, including their roles.
     */
    private List<TeacherSubjectRoles> subjects;
}