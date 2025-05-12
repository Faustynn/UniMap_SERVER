package org.main.unimapapi.models;

import java.time.LocalDateTime;

public class TempUser {
    private Long idUser;
    private LocalDateTime createdAt;
    private int deleteAfterMinutes;

    public TempUser(Long idUser, LocalDateTime createdAt, int deleteAfterMinutes) {
        this.idUser = idUser;
        this.createdAt = createdAt;
        this.deleteAfterMinutes = deleteAfterMinutes;
    }

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getDeleteAfterMinutes() { return deleteAfterMinutes; }
    public void setDeleteAfterMinutes(int deleteAfterMinutes) { this.deleteAfterMinutes = deleteAfterMinutes; }
}
