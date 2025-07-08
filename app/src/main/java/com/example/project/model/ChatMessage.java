
package com.example.project.model;
import com.google.firebase.Timestamp;

import java.io.Serializable;

public class ChatMessage {
    private String id; // Unique ID for the message
    private String text;
    private Timestamp timestamp;
    private String senderId; // ID of the user who sent the message
    private boolean isSentByUser; // True if the current user sent this message

    public ChatMessage(String id, String text, Timestamp timestamp, String senderId, boolean isSentByUser) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.isSentByUser = isSentByUser;
    }

    public ChatMessage() {
        // Required empty constructor for Firestore deserialization
    }

    // Getters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public Timestamp getTimestamp() { return timestamp; }

    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

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
