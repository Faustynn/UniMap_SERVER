package org.main.unimapapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a comment associated with a subject or teacher.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment_dto {

    /**
     * The ID of the user who wrote the comment.
     */
    private int user_id;

    /**
     * The display name of the subject or teacher the comment is about.
     */
    private String name;

    /**
     * The ID of the target (subject or teacher) the comment refers to.
     */
    private String looking_id;

    /**
     * The content of the comment.
     */
    private String description;

    /**
     * The rating given by the user (e.g., \"5 stars\").
     */
    private String rating;

    /**
     * Access level of the comment (e.g., public, private, or for moderation).
     */
    private int levelAccess;

    /**
     * The unique identifier of the comment itself.
     */
    private int comment_id;
}