package com.example.project.dto;

public class CreateChatMessageDto {
    private int userID;
    private String message;
    private String sentAt;

    public CreateChatMessageDto() {}

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }
}
