package org.main.unimapapi.entities;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Teacher {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String office;
    private List<TeacherSubjectRoles> subjects;
}
