package com.lww.controller;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatController {

    private final ChatLanguageModel chatModel;

    public ChatController(ChatLanguageModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return chatModel.generate(request.getInput());
    }
}
