package com.lww.medical.dto;

import java.util.*;

public class ChatResponse {
    private String reply;
    private List<String> toolCalls = new ArrayList<>();
    private List<String> knowledgeSources = new ArrayList<>();
    private String sessionId;
    private boolean emergency;

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public List<String> getToolCalls() { return toolCalls; }
    public void setToolCalls(List<String> toolCalls) { this.toolCalls = toolCalls; }
    public List<String> getKnowledgeSources() { return knowledgeSources; }
    public void setKnowledgeSources(List<String> knowledgeSources) { this.knowledgeSources = knowledgeSources; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public boolean isEmergency() { return emergency; }
    public void setEmergency(boolean emergency) { this.emergency = emergency; }
}
