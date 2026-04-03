package com.lww.controller;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentController {

    private final AgentAssistant agentAssistant;

    public AgentController(AgentAssistant agentAssistant) {
        this.agentAssistant = agentAssistant;
    }

    @PostMapping("/agent")
    public String agent(@RequestBody ChatRequest request) {
        return agentAssistant.chat(request.getInput());
    }

    /**
     * AiServices 会把该接口方法当作 Agent 的入口。
     * 通过 SystemMessage/用户消息模板引导“何时使用工具”。
     */
    public interface AgentAssistant {

        @SystemMessage("你是一个中文智能助手。\n"
                + "你拥有工具能力：当用户需要“实时确定信息”（例如当前时间/时间戳换算）时，必须先调用工具获得结果。\n"
                + "当用户需要“知识库内容”（例如：让你查询某段知识/回答需要依据你写入的知识库）时，必须先调用工具 kb_search 获取相关片段，并只基于片段回答。\n"
                + "当用户要求你新增/更新知识库时，必须调用 kb_put 写入。\n"
                + "当用户要求按 kbId 读取知识库时，必须调用 kb_get。\n"
                + "回答要简洁、准确，并优先引用工具返回的内容。\n"
                + "回答要简洁、准确。")
        @UserMessage("{{it}}")
        String chat(String userMessage);
    }
}
