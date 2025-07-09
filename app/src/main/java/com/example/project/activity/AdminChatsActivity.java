package com.example.project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import com.example.project.adapter.AdminChatsAdapter;
import com.example.project.model.Chat;
import com.example.project.utils.FirebaseUtil;
import com.example.project.utils.UserManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminChatsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAdminChats;
    private AdminChatsAdapter adapter;
    private Toolbar toolbarAdminChats;

    private String adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chats);

        toolbarAdminChats = findViewById(R.id.toolbarAdminChats);
        setSupportActionBar(toolbarAdminChats);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Chats");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        UserManager userManager = new UserManager(this);
        adminId = userManager.getUser().getId();

        recyclerViewAdminChats = findViewById(R.id.recyclerViewAdminChats);
        recyclerViewAdminChats.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminChatsAdapter(chat -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("otherUserId", getOtherUserId(chat.getParticipants(), adminId));
            intent.putExtra("chatId", chat.getId());
            startActivity(intent);
        });

        recyclerViewAdminChats.setAdapter(adapter);

        loadAdminChats();
    }

    private void loadAdminChats() {
        FirebaseUtil.getAdminChats(adminId)
                .addSnapshotListener(this, (snapshots, e) -> {
                    if (e != null) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load chats", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (snapshots != null) {
                        List<Chat> chatList = new ArrayList<>();
                        for (var doc : snapshots.getDocuments()) {
                            Chat chat = doc.toObject(Chat.class);
                            if (chat != null) {
                                chat.setId(doc.getId());
                                chatList.add(chat);
                            }
                        }
                        adapter.submitList(chatList);
                    }
                });
    }

    private String getOtherUserId(List<String> participants, String adminId) {
        for (String id : participants) {
            if (!id.equals(adminId)) return id;
        }
        return "";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
