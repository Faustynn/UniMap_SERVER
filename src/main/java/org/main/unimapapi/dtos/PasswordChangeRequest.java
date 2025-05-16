package org.main.unimapapi.dtos;

import lombok.Data;

/**
 * DTO used for requesting a password change for a user.
 *
 * <p>Typically used in password recovery or profile update flows.</p>
 */
@Data
public class PasswordChangeRequest {

    /**
     * Email of the user requesting the password change.
     */
    private String email;

    /**
     * The new password to be set.
     */
    private String newPassword;
}