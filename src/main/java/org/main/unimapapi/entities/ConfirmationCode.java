package org.main.unimapapi.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a confirmation code used for verifying user identity,
 * such as during email verification or password reset.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationCode {

    /**
     * ID of the user associated with this confirmation code.
     */
    private Long userId;

    /**
     * The generated confirmation code string (e.g., 6-digit or alphanumeric).
     */
    private String code;

    /**
     * The expiration timestamp after which the code becomes invalid.
     */
    private LocalDateTime expirationTime;
}