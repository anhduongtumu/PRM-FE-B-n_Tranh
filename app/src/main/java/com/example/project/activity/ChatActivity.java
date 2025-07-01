package com.example.project.activity; // Adjust package name

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem; // Needed for handling Toolbar item clicks
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull; // For @NonNull annotation
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Import Toolbar
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import com.example.project.adapter.ChatAdapter;
import com.example.project.model.ChatMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messagesList = new ArrayList<>();

    private final String currentUserId = "currentUser123";
    private final String otherUserId = "otherUser456";

    private Toolbar toolbarChat; // Declare Toolbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Toolbar
        toolbarChat = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbarChat); // Set Toolbar as ActionBar

        // Enable the Up button (back arrow)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Chat"); // Set a title for the chat screen
        }

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        setupRecyclerView();
        loadInitialMessages();

        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(currentUserId);
        recyclerViewChat.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(layoutManager);
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            ChatMessage newMessage = new ChatMessage(
                    UUID.randomUUID().toString(),
                    messageText,
                    System.currentTimeMillis(),
                    currentUserId,
                    true
            );
            addNewMessageToList(newMessage);
            editTextMessage.setText("");
            simulateReply(messageText);
        }
    }

    private void simulateReply(String originalText) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ChatMessage replyMessage = new ChatMessage(
                    UUID.randomUUID().toString(),
                    "Okay, Java sees you said: \"" + originalText + "\"",
                    System.currentTimeMillis(),
                    otherUserId,
                    false
            );
            addNewMessageToList(replyMessage);
        }, 1200);
    }

    private void addNewMessageToList(ChatMessage message) {
        messagesList.add(message);
        chatAdapter.submitList(new ArrayList<>(messagesList));
        if (chatAdapter.getItemCount() > 0) {
            recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    private void loadInitialMessages() {
        messagesList.add(new ChatMessage("1", "Hey there from Java!", System.currentTimeMillis() - 600000, otherUserId, false));
        messagesList.add(new ChatMessage("2", "Hi! How's it going in Java?", System.currentTimeMillis() - 500000, currentUserId, true));
        chatAdapter.submitList(new ArrayList<>(messagesList));
        if (!messagesList.isEmpty()) {
            recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    // Handle Toolbar item selections (specifically the Up button)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Check if the Up button (back arrow) was pressed
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to the previous activity (MainActivity in this case)
            // or finish the current activity.
            // Using finish() is common if ChatActivity was started directly from MainActivity
            // and you expect the standard back stack behavior.
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}