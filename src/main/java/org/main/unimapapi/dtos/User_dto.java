package org.main.unimapapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User_dto {
    @NotBlank(message = "Login is mandatory")
    @Size(min = 3, max = 20, message = "Login must be between 3 and 20 characters")
    private String login;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    private boolean isAdmin;
    private boolean subscribe;
    private boolean verification;
    private int avatar;

}