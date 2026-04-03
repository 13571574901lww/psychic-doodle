package com.lww.controller;

public class ChatRequest {
    private String input;

    public ChatRequest() {}

    public ChatRequest(String input) {
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}

