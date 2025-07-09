package com.example.project.activity; // Adjust package name

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem; // Needed for handling Toolbar item clicks
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull; // For @NonNull annotation
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Import Toolbar
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import com.example.project.adapter.ChatAdapter;
import com.example.project.model.ChatMessage;
import com.example.project.utils.FirebaseUtil;
import com.example.project.utils.UserManager;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messagesList = new ArrayList<>();

    private String currentUserId;
    private String customerName;
    private String otherUserId; // admin UID
    private String chatId;

    private UserManager userManager;

    private Toolbar toolbarChat; // Declare Toolbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbarChat = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbarChat);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Support Chat");
        }

        userManager = new UserManager(this);
        currentUserId = userManager.getUser().getId();
        customerName = userManager.getUser().getUsername();

        // Get otherUserId from intent (default to "1" if missing)
        otherUserId = getIntent().getStringExtra("otherUserId");
        if (TextUtils.isEmpty(otherUserId)) {
            otherUserId = "1";
        }

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        setupRecyclerView();
        if (!currentUserId.equals("1")){
            chatId = FirebaseUtil.generateChatId(currentUserId, otherUserId);


            FirebaseUtil.createChatIfNotExists(
                    chatId,
                    currentUserId,
                    otherUserId,
                    customerName,
                    unused -> loadMessagesFromFirestore(),
                    e -> Toast.makeText(this, "Failed to create chat: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        } else {
            chatId = getIntent().getStringExtra("chatId");
            loadMessagesFromFirestore();
        }


        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(currentUserId);
        recyclerViewChat.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(layoutManager);
    }

//    private void sendMessage() {
//        String messageText = editTextMessage.getText().toString().trim();
//        if (!messageText.isEmpty()) {
//            ChatMessage newMessage = new ChatMessage(
//                    UUID.randomUUID().toString(),
//                    messageText,
//                    System.currentTimeMillis(),
//                    currentUserId,
//                    true
//            );
//            addNewMessageToList(newMessage);
//            editTextMessage.setText("");
//            simulateReply(messageText);
//        }
//    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            FirebaseUtil.sendMessage(
                    currentUserId,
                    otherUserId,
                    messageText,
                    unused -> editTextMessage.setText(""),
                    e -> Toast.makeText(this, "Failed to send: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        }
    }


//    private void simulateReply(String originalText) {
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            ChatMessage replyMessage = new ChatMessage(
//                    UUID.randomUUID().toString(),
//                    "Okay, Java sees you said: \"" + originalText + "\"",
//                    System.currentTimeMillis(),
//                    otherUserId,
//                    false
//            );
//            addNewMessageToList(replyMessage);
//        }, 1200);
//    }

    private void addNewMessageToList(ChatMessage message) {
        messagesList.add(message);
        chatAdapter.submitList(new ArrayList<>(messagesList));
        if (chatAdapter.getItemCount() > 0) {
            recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

//    private void loadInitialMessages() {
////        FirebaseUtil.getMessagesBySender("1").get().addOnCompleteListener(task -> {
////            if (task.isSuccessful()){
////                messagesList
////
////            }
////        });
//        messagesList.add(new ChatMessage("1", "Hey there from Java!", System.currentTimeMillis() - 600000, otherUserId, false));
//        messagesList.add(new ChatMessage("2", "Hi! How's it going in Java?", System.currentTimeMillis() - 500000, currentUserId, true));
//        chatAdapter.submitList(new ArrayList<>(messagesList));
//        if (!messagesList.isEmpty()) {
//            recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
//        }
//    }

    private void loadMessagesFromFirestore() {
        FirebaseUtil.getMessagesInChat(chatId)
            .orderBy("timestamp")
            .addSnapshotListener(this, (snapshots, e) -> {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                if (snapshots != null) {
                    boolean shouldScroll = false;
                    for (var dc : snapshots.getDocumentChanges()) {
                        DocumentSnapshot docSnap = dc.getDocument();

                        android.util.Log.d("ChatSnapshot", "Doc ID: " + docSnap.getId()
                                + " | Pending Writes: " + docSnap.getMetadata().hasPendingWrites()
                                + " | Data: " + docSnap.getData());

                        ChatMessage message = docSnap.toObject(ChatMessage.class);
                        message.setId(docSnap.getId());
                        // Log the timestamp for debugging
                        Timestamp ts = message.getTimestamp();
                        if (ts != null) {
                            android.util.Log.d("ChatTimestamp", "Message ID: " + message.getId() +
                                    " | Timestamp: " + ts.toDate().toString());
                        } else {
                            android.util.Log.d("ChatTimestamp", "Message ID: " + message.getId() +
                                    " | Timestamp: null (not yet set by Firestore)");
                        }
                        switch (dc.getType()) {
                            case ADDED:
                                if (messagesList.stream().noneMatch(m -> m.getId().equals(message.getId()))) {
                                    messagesList.add(message);
                                    shouldScroll = true;
                                }
                                break;
                            case MODIFIED:
                                for (int i = 0; i < messagesList.size(); i++) {
                                    if (messagesList.get(i).getId().equals(message.getId())) {
                                        messagesList.set(i, message);
                                        shouldScroll = true;
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                messagesList.removeIf(m -> m.getId().equals(message.getId()));
                                break;
                        }
                    }
                    chatAdapter.submitList(new ArrayList<>(messagesList));
                    if (shouldScroll) {
                        recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                }
            });

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