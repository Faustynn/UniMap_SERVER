package org.main.unimapapi.dtos;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class News_dto {
    private int id;
    private String title;
    private String content;
    private String date_of_creation;
}
