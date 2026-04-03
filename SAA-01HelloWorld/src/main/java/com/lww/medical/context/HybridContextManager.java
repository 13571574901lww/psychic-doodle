package com.lww.medical.context;

import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 混合上下文管理器
 */
@Component
public class HybridContextManager {

    private final ChatLanguageModel chatModel;
    private final Map<String, HybridContext> sessions = new HashMap<>();

    public HybridContextManager(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    public HybridContext getOrCreateContext(String sessionId) {
        return sessions.computeIfAbsent(sessionId, k -> new HybridContext());
    }

    public void addMessage(String sessionId, ChatMessage message) {
        HybridContext context = getOrCreateContext(sessionId);
        context.addMessage(message);

        // 每10轮生成摘要
        if (context.needsSummary()) {
            generateSummary(sessionId);
        }
    }

    private void generateSummary(String sessionId) {
        HybridContext context = sessions.get(sessionId);
        if (context == null) return;

        String prompt = "请总结以下对话的关键医疗信息（症状、诊断建议、用药）：\n" +
                        context.getShortTermMessages().toString();

        String summary = chatModel.generate(prompt);
        context.setLongTermSummary(summary);
    }

    public List<ChatMessage> buildContextMessages(String sessionId) {
        HybridContext context = sessions.get(sessionId);
        if (context == null) return new ArrayList<>();

        List<ChatMessage> messages = new ArrayList<>();

        // 添加长期摘要
        if (!context.getLongTermSummary().isEmpty()) {
            messages.add(new SystemMessage("历史摘要：" + context.getLongTermSummary()));
        }

        // 添加短期对话
        messages.addAll(context.getShortTermMessages());

        return messages;
    }
}
