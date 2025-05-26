package com.shiftmanagement.app_core.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.swagger.v3.oas.annotations.media.Schema;

@Document(collection = "Shifts")
public class Shift {
    @Id
    @Schema(hidden = true)
    private String id;
    private String userId;
    private String username;
    private String specialty;

    @Schema(hidden = true)
    private String turnCode;
    @Schema(hidden = true)      
    private boolean specialPriority;

    @Schema(hidden = true)
    private LocalDateTime createdAt;
    @Schema(hidden = true)
    private ShiftStatus status;

    @Schema(hidden = true)
    private String userRole;

    public Shift(String userId, String specialty, boolean specialPriority) {
        this.userId = userId;
        this.specialty = specialty;
        this.specialPriority = specialPriority;
    }


    public Shift() {
    }


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getSpecialty() {
        return specialty;
    }
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
    public String getTurnCode() {
        return turnCode;
    }
    public void setTurnCode(String turnCode) {
        this.turnCode = turnCode;
    }
    public boolean isSpecialPriority() {
        return specialPriority;
    }
    public void setSpecialPriority(boolean specialPriority) {
        this.specialPriority = specialPriority;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public ShiftStatus getStatus() {
        return status;
    }
    public void setStatus(ShiftStatus status) {
        this.status = status;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    

}
