package org.main.unimapapi.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO representing a user for registration or account management.
 *
 * <p>Includes validation rules for login, email, password, and username.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User_dto {

    /**
     * Unique login identifier for the user (3–20 characters).
     */
    @NotBlank(message = "Login is mandatory")
    @Size(min = 3, max = 20, message = "Login must be between 3 and 20 characters")
    private String login;

    /**
     * Email address of the user. Must be valid and not empty.
     */
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * Encrypted or raw password for registration. Must be at least 8 characters.
     */
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 6 characters")
    private String password;

    /**
     * Full display name or username of the user (3–50 characters).
     */
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /**
     * Flag indicating if the user is an administrator.
     */
    private boolean isAdmin;

    /**
     * Flag indicating if the user has premium status.
     */
    private boolean isPremium;

    /**
     * Binary data of the user's avatar image.
     */
    private byte[] avatarBinary;

    /**
     * File name of the uploaded avatar image.
     */
    private String avatarFileName;
}