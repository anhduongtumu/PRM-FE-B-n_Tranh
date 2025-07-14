package com.example.project.model;
import com.google.firebase.Timestamp;
import java.util.List;

public class Chat {
    private String id; // chatId
    private String lastMessage;
    private Timestamp lastMessageTimestamp;

    private String customerName;
    private List<String> participants;

    public Chat() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerName() {return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }


    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public Timestamp getLastMessageTimestamp() { return lastMessageTimestamp; }
    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) { this.lastMessageTimestamp = lastMessageTimestamp; }

    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }
}

