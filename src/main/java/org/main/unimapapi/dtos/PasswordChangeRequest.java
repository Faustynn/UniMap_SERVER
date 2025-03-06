package org.main.unimapapi.dtos;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String email;
    private String newPassword;
}