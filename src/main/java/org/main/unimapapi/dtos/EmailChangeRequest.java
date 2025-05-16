package org.main.unimapapi.dtos;

import lombok.Data;

/**
 * DTO for changing the user's email address.
 *
 * <p>Used in endpoints that allow users to update their registered email.</p>
 */
@Data
public class EmailChangeRequest {

    /**
     * The login (username) of the user whose email is being changed.
     */
    private String login;

    /**
     * The new email address to associate with the user.
     */
    private String email;
}
