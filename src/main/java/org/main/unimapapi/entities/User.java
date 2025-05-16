package org.main.unimapapi.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Entity representing a user of the UniMap system.
 *
 * <p>Contains authentication details, profile data, and role flags.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Unique identifier for the user.
     */
    private Long id;

    /**
     * Login name used for authentication (usually unique).
     */
    private String login;

    /**
     * User's registered email address.
     */
    private String email;

    /**
     * Encrypted password (may be null when sending user in responses).
     */
    private String password;

    /**
     * Publicly visible display name of the user.
     */
    private String username;

    /**
     * Indicates whether the user has administrator privileges.
     */
    private boolean isAdmin;

    /**
     * Indicates whether the user has premium status.
     */
    private boolean isPremium;

    /**
     * Binary data representing the user's avatar image.
     */
    private byte[] avatar;

    /**
     * File name of the uploaded avatar (optional).
     */
    private String avatarFileName;
}