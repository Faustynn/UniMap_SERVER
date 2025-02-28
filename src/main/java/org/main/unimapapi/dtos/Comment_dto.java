package org.main.unimapapi.dtos;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comment_dto {
    private int user_id;
    // looking can be like subject_id or teacher_id
    private String looking_id;
    private String description;
    private int rating;
}
