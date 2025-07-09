package com.example.project.utils;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FirebaseUtil {

    /**
     * User: Load the chat between user and admin
     * @param userId UID of the current user
     * @param adminId UID of the admin (e.g., "admin1")
     */
    public static Query getUserSupportChat(String userId, String adminId) {
        return FirebaseFirestore.getInstance()
                .collection("chats")
                .whereArrayContains("participants", userId)
                .whereArrayContains("participants", adminId)
                .limit(1);
    }

    /**
     * Admin: Load all chats with customers
     * @param adminId UID of the admin (e.g., "admin1")
     */
    public static Query getAdminChats(String adminId) {
        return FirebaseFirestore.getInstance()
                .collection("chats")
//                .whereArrayContains("participants", adminId)
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);
    }

    /**
     * Get reference to messages in a chat
     * @param chatId ID of the chat document
     */
    public static CollectionReference getMessagesInChat(String chatId) {
        return FirebaseFirestore.getInstance()
                .collection("chats")
                .document(chatId)
                .collection("messages");
    }


    /**
     * Generates a consistent chatId for two participants.
     */
    public static String generateChatId(String uid1, String uid2) {
        // lexicographically sort to get the same ID for both directions
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }

    /**
     * Send a chat message between senderId and receiverId.
     * - Creates the chat document if missing (with participants array).
     * - Adds the message to chats/{chatId}/messages.
     * - Updates chats/{chatId} metadata: lastMessage, lastMessageTimestamp.
     */
    public static void sendMessage(
            String senderId,
            String receiverId,
            String text,
            OnSuccessListener<Void> onSuccess,
            OnFailureListener onFailure
    ) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String chatId = generateChatId(senderId, receiverId);

        DocumentReference chatRef = db.collection("chats").document(chatId);
        CollectionReference messagesRef = chatRef.collection("messages");

        // 1) Ensure chat exists with participants
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("participants", Arrays.asList(senderId, receiverId));
        // (optional) you could set a default lastMessage/time here or leave blank
        chatRef.set(chatData, SetOptions.merge());

        // 2) Prepare message payload
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("senderId", senderId);
        messageData.put("receiverId", receiverId);
        messageData.put("text", text);
        messageData.put("timestamp", FieldValue.serverTimestamp());

        // 3) Batch write: add message + update chat metadata
        WriteBatch batch = db.batch();

        DocumentReference newMsgRef = messagesRef.document();  // auto-ID
        batch.set(newMsgRef, messageData);

        batch.update(chatRef,
                "lastMessage", text,
                "lastMessageTimestamp", FieldValue.serverTimestamp()
        );

        // 4) Commit batch
        batch.commit()
             .addOnSuccessListener(onSuccess)
             .addOnFailureListener(onFailure);
    }

    public static void createChatIfNotExists(String customerName, String chatId, String userId1, String userId2, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        DocumentReference chatDocRef = FirebaseFirestore.getInstance()
                .collection("chats")
                .document(chatId);

        chatDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Map<String, Object> chatData = new HashMap<>();
                chatData.put("participants", Arrays.asList(userId1, userId2));
                chatData.put("customerName",customerName );
                chatData.put("lastMessage", "");
                chatData.put("lastUpdated", System.currentTimeMillis());

                chatDocRef.set(chatData)
                        .addOnSuccessListener(onSuccess)
                        .addOnFailureListener(onFailure);
            } else {
                onSuccess.onSuccess(null); // Already exists, proceed
            }
        }).addOnFailureListener(onFailure);
    }

}
