package com.example.hopstack;

public class ChatMessage {
    private String message;
    private boolean isUser; // True if user message, False if bot response

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}
