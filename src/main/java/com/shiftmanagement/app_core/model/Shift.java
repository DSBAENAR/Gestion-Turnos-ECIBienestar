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

    private String turnCode;         // e.g., D-05
    private boolean specialPriority; // true = pregnancy, disability

    private LocalDateTime createdAt;
    private AppointmentStatus status;

    public Shift(String userId, String specialtyId) {
        this.userId = userId;
        this.specialtyId = specialtyId;
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
    public AppointmentStatus getStatus() {
        return status;
    }
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    

}
