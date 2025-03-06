package org.main.unimapapi.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationCode {
    private Long userId;
    private String code;
    private LocalDateTime expirationTime;
}