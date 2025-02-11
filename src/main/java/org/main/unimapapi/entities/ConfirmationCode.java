package org.main.unimapapi.entities;


import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "confirm_codes")
public class ConfirmationCode {
    @Id
    @Column(name = "id_code", nullable = false)
    private Long userId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "exp_time", nullable = false)
    private LocalDateTime expirationTime;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}

