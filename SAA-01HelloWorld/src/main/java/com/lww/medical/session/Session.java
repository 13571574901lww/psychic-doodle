package com.lww.medical.session;

import java.time.LocalDateTime;

/**
 * 会话实体
 */
public class Session {
    private String sessionId;
    private String patientId;
    private SessionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastActiveAt;

    public Session(String sessionId, String patientId) {
        this.sessionId = sessionId;
        this.patientId = patientId;
        this.status = SessionStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.lastActiveAt = LocalDateTime.now();
    }

    public void updateActivity() {
        this.lastActiveAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return lastActiveAt.plusMinutes(30).isBefore(LocalDateTime.now());
    }

    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public String getPatientId() { return patientId; }
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastActiveAt() { return lastActiveAt; }
}
