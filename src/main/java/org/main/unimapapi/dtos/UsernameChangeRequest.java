package org.main.unimapapi.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO representing a request to change a user's username.
 *
 * <p>Used in endpoints where a user updates their visible name based on their email address.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernameChangeRequest {

    /**
     * The email of the user requesting the username change.
     */
    private String email;

    /**
     * The new username to be set for the user.
     */
    private String username;
}