package org.main.unimapapi.dtos;

import lombok.Data;

/**
 * DTO representing a request to change a user's avatar.
 *
 * <p>This object is typically used in endpoints where the user provides binary avatar data
 * and associates it with their account using their email.</p>
 */
@Data
public class AvatarChangeRequest {
    /**
     * The email of the user whose avatar is being changed.
     */
    private String email;

    /**
     * The binary content of the new avatar image.
     */
    private byte[] avatarBinary;
}