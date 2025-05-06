package com.shiftmanagement.app_core.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Shifts")
public class Shift {
    @Id
    private String id;

    private String userId;
    private String specialtyId;

    private String turnCode;         
    private boolean specialPriority;

    private LocalDateTime createdAt;
    private ShiftStatus status;

    private String role;

    public Shift(String userId) {
        this.userId = userId;
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
    public String getSpecialtyId() {
        return specialtyId;
    }
    public void setSpecialtyId(String specialtyId) {
        this.specialtyId = specialtyId;
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

    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }

}
