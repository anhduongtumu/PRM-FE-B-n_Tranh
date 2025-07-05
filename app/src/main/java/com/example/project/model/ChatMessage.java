
package com.example.project.model;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private String id; // Unique ID for the message
    private String text;
    private long timestamp;
    private String senderId; // ID of the user who sent the message
    private boolean isSentByUser; // True if the current user sent this message

    public ChatMessage(String id, String text, long timestamp, String senderId, boolean isSentByUser) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.isSentByUser = isSentByUser;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    // It's good practice to also have setters if you might modify these fields after creation,
    // but for an immutable-by-default message, getters are often enough.
    // If you use DiffUtil, you might also want to override equals() and hashCode().
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return id.equals(that.id); // Typically, ID is enough for item identity
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
