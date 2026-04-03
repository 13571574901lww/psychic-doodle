package com.lww.medical.context;

import dev.langchain4j.data.message.ChatMessage;
import java.util.*;

/**
 * 混合上下文 - 整合多种策略
 */
public class HybridContext {
    private List<ChatMessage> shortTermMessages = new ArrayList<>();
    private String longTermSummary = "";
    private Map<String, Object> entities = new HashMap<>();
    private int messageCount = 0;

    public void addMessage(ChatMessage message) {
        shortTermMessages.add(message);
        messageCount++;

        // 保持滑动窗口大小
        if (shortTermMessages.size() > 8) {
            shortTermMessages.remove(0);
        }
    }

    public List<ChatMessage> getShortTermMessages() {
        return new ArrayList<>(shortTermMessages);
    }

    public void setLongTermSummary(String summary) {
        this.longTermSummary = summary;
    }

    public String getLongTermSummary() {
        return longTermSummary;
    }

    public void addEntity(String key, Object value) {
        entities.put(key, value);
    }

    public Map<String, Object> getEntities() {
        return new HashMap<>(entities);
    }

    public int getMessageCount() {
        return messageCount;
    }

    public boolean needsSummary() {
        return messageCount > 0 && messageCount % 10 == 0;
    }
}
