package com.example.project.utils;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FirebaseUtil {
    public static Query getMessagesBySender(String senderId) {
        return FirebaseFirestore.getInstance()
                .collectionGroup("messages")
                .whereEqualTo("senderId", senderId);
    }
}
