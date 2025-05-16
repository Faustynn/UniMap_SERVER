package org.main.unimapapi.dtos;

import lombok.*;

/**
 * DTO representing a news item in the system.
 *
 * <p>Contains basic metadata like title, content, location and creation date.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class News_dto {

    /**
     * Unique identifier of the news item.
     */
    private int id;

    /**
     * Title of the news item.
     */
    private String title;

    /**
     * Full content or body of the news item.
     */
    private String content;

    /**
     * Optional coordinates associated with the news (e.g., event location).
     */
    private Coordinates_dto coordinates;

    /**
     * Date when the news item was created (formatted as string).
     */
    private String date_of_creation;
}



