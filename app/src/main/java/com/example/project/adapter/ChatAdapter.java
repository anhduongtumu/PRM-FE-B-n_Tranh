package com.example.project.adapter; // Adjust package name

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R; // Adjust if your R file is in a different location
import com.example.project.model.ChatMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends ListAdapter<ChatMessage, RecyclerView.ViewHolder> {

    private final String currentUserId;
    private final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(String currentUserId) {
        super(new ChatMessageDiffCallback());
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItem(position);
        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = getItem(position);
        String formattedTime = sdf.format(new Date(message.getTimestamp()));

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message, formattedTime);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message, formattedTime);
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timestampText;

        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textViewMessageContent);
            timestampText = itemView.findViewById(R.id.textViewMessageTimestamp);
        }

        void bind(ChatMessage message, String formattedTime) {
            messageText.setText(message.getText());
            timestampText.setText(formattedTime);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        // TextView senderNameText; // If you want to show sender name
        TextView messageText;
        TextView timestampText;

        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            // senderNameText = itemView.findViewById(R.id.textViewSenderName);
            messageText = itemView.findViewById(R.id.textViewMessageContent);
            timestampText = itemView.findViewById(R.id.textViewMessageTimestamp);
        }

        void bind(ChatMessage message, String formattedTime) {
            // senderNameText.setText("Sender: " + message.getSenderId()); // Example
            // senderNameText.setVisibility(View.VISIBLE);
            messageText.setText(message.getText());
            timestampText.setText(formattedTime);
        }
    }

    static class ChatMessageDiffCallback extends DiffUtil.ItemCallback<ChatMessage> {
        @Override
        public boolean areItemsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            // For simple data class, you might compare all fields or rely on a well-defined equals()
            // For this example, if IDs are same and texts are same, we consider content same.
            // A more robust equals() in ChatMessage would be better.
            return oldItem.getText().equals(newItem.getText()) &&
                    oldItem.getTimestamp() == newItem.getTimestamp() &&
                    oldItem.isSentByUser() == newItem.isSentByUser();
        }
    }
}
