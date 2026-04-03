package com.lww.configer;

import com.lww.controller.AgentController;
import com.lww.kb.KnowledgeBaseService;
import com.lww.medical.tools.MedicalTools;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Instant;

@Configuration
public class LangChina4JConfig {

    /**
     * 复用你现有的 Spring AI 配置：application.yml 里的 `spring.ai.zhipuai.api-key`
     */
    @Value("${spring.ai.zhipuai.api-key}")
    private String zhipuApiKey;

    private Duration timeout = Duration.ofSeconds(60);

    @Bean
    public ChatLanguageModel dashscopeChatLanguageModel() {
        // 使用智谱 GLM 模型
        return dev.langchain4j.model.zhipu.ZhipuAiChatModel.builder()
                .apiKey(zhipuApiKey)
                .model("glm-4-flash")
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .writeTimeout(timeout)
                .callTimeout(timeout)
                .build();
    }

    @Bean
    public EmbeddingModel kbEmbeddingModel() {
        // 使用智谱 embedding 模型
        return dev.langchain4j.model.zhipu.ZhipuAiEmbeddingModel.builder()
                .apiKey(zhipuApiKey)
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .writeTimeout(timeout)
                .callTimeout(timeout)
                .build();
    }

    @Bean
    public MedicalTools medicalTools(KnowledgeBaseService knowledgeBaseService) {
        return new MedicalTools(knowledgeBaseService);
    }

    @Bean
    public KnowledgeBaseService knowledgeBaseService(@Qualifier("kbEmbeddingModel") EmbeddingModel embeddingModel) {
        return new KnowledgeBaseService(embeddingModel);
    }

    @Bean
    public AgentController.AgentAssistant agentAssistant(ChatLanguageModel dashscopeChatLanguageModel,
                                                          KnowledgeBaseService knowledgeBaseService) {
        return AiServices.builder(AgentController.AgentAssistant.class)
                .chatLanguageModel(dashscopeChatLanguageModel)
                .tools(new AgentTools(knowledgeBaseService))
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    /**
     * 让智能体具备“工具调用能力”：当用户问到实时信息（如当前时间）时，
     * 模型会选择调用这些方法，把结果返回给模型再形成最终答复。
     */
    static class AgentTools {

        private final KnowledgeBaseService knowledgeBaseService;

        AgentTools(KnowledgeBaseService knowledgeBaseService) {
            this.knowledgeBaseService = knowledgeBaseService;
        }

        @Tool("获取服务器当前时间（UTC，ISO-8601 格式）")
        public String nowUtcIso() {
            return Instant.now().toString();
        }

        @Tool("把 UTC 时间毫秒时间戳转换为 ISO-8601 格式字符串")
        public String epochMillisToUtcIso(@P("UTC epoch 毫秒时间戳") long epochMillis) {
            return Instant.ofEpochMilli(epochMillis).toString();
        }

        @Tool("写入知识库：参数 kbId(唯一id)、title(标题)、content(内容)。返回写入结果")
        public String kb_put(@P("kbId") String kbId,
                               @P("title") String title,
                               @P("content") String content) {
            return knowledgeBaseService.put(kbId, title, content);
        }

        @Tool("读取知识库：参数 kbId。返回该条知识内容或未找到信息")
        public String kb_get(@P("kbId") String kbId) {
            return knowledgeBaseService.get(kbId);
        }

        @Tool("搜索知识库：参数 query(查询语句)、topK(返回条数)。返回最相关的知识片段")
        public String kb_search(@P("query") String query,
                                  @P("topK") Integer topK) {
            return knowledgeBaseService.search(query, topK == null ? 5 : topK);
        }

        @Tool("HTTP GET 请求并返回响应正文（最多返回 10000 字符）。仅用于调用你可信的业务接口。")
        public String http_get(@P("url") String url) {
            if (url == null || url.trim().isEmpty()) {
                return "http_get 失败：url 不能为空";
            }
            String target = url.trim();

            try {
                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(target))
                        .GET()
                        .build();

                java.net.http.HttpResponse<String> response =
                        client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString(java.nio.charset.StandardCharsets.UTF_8));

                String body = response.body();
                if (body != null && body.length() > 10000) {
                    body = body.substring(0, 10000) + "...";
                }

                return "http_get status=" + response.statusCode() + ", body=" + body;
            } catch (Exception e) {
                return "http_get 出错：" + e.getMessage();
            }
        }
    }

}
