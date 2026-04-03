package com.lww.medical.dto;

public class ChatRequest {
    private String sessionId;
    private String patientId;
    private String message;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
