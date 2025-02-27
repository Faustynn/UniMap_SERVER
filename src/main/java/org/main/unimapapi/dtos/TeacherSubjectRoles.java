package org.main.unimapapi.dtos;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TeacherSubjectRoles {
    private String subjectName;
    private List<String> roles;
}
