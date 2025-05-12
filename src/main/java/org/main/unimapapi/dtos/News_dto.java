package org.main.unimapapi.dtos;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class News_dto {
    private int id;
    private String title;
    private String content;
    private Coordinates_dto coordinates;
    private String date_of_creation;
}



