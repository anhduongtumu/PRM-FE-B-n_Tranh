package com.example.project.service;

import com.example.project.model.ChatMessage;
import com.example.project.dto.CreateChatMessageDto;
import com.example.project.dto.UpdateChatMessageDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ChatMessageService {
    @GET("/api/chatmessages")
    Call<List<ChatMessage>> getAllChatMessages();

    @GET("/api/chatmessages/{id}")
    Call<ChatMessage> getChatMessageById(@Path("id") int id);

    @GET("/api/chatmessages/user/{userId}")
    Call<List<ChatMessage>> getChatMessagesByUserId(@Path("userId") int userId);

    @POST("/api/chatmessages")
    Call<ChatMessage> createChatMessage(@Body CreateChatMessageDto chatMessage);

    @PUT("/api/chatmessages/{id}")
    Call<ChatMessage> updateChatMessage(@Path("id") int id, @Body UpdateChatMessageDto chatMessage);

    @DELETE("/api/chatmessages/{id}")
    Call<Void> deleteChatMessage(@Path("id") int id);
}
