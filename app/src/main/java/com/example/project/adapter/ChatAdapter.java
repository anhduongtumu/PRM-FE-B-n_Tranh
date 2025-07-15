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
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends ListAdapter<ChatMessage, RecyclerView.ViewHolder> {

    private final int currentUserId;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy, h:mm a", Locale.getDefault());

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(int currentUserId) {
        super(new ChatMessageDiffCallback());
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = getItem(position);
        if (message.getSenderId().equals(String.valueOf(currentUserId))) {
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
        Timestamp ts = message.getTimestamp();
        String formattedTime = formatTimestamp(ts);


        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message, formattedTime);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message, formattedTime);
        }
    }

    private String formatTimestamp(Timestamp ts) {
        if (ts == null) return "Đang gửi...";

        Date date = ts.toDate();
        Date today = new Date();

        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String dateString = dayFormat.format(date);
        String todayString = dayFormat.format(today);

        if (dateString.equals(todayString)) {
            // If today, show only time
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return timeFormat.format(date);
        } else {
            // Else, show date + time
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yy, h:mm a", Locale.getDefault());
            return dateTimeFormat.format(date);
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
            boolean result = safeEquals(oldItem.getId(), newItem.getId());
            android.util.Log.d("ChatDiffUtil", "areItemsTheSame: oldId=" + oldItem.getId()
                    + ", newId=" + newItem.getId() + " -> " + result);
            return result;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            boolean textSame = safeEquals(oldItem.getText(), newItem.getText());
            boolean timestampSame = safeEquals(oldItem.getTimestamp(), newItem.getTimestamp());
            boolean sentByUserSame = oldItem.isSentByUser() == newItem.isSentByUser();
            boolean result = textSame && timestampSame && sentByUserSame;

//            android.util.Log.d("ChatDiffUtil", "areContentsTheSame:"
//                    + "\n   oldText=" + oldItem.getText()
//                    + ", newText=" + newItem.getText()
//                    + " -> " + textSame
//                    + "\n   oldTimestamp=" + oldItem.getTimestamp()
//                    + ", newTimestamp=" + newItem.getTimestamp()
//                    + " -> " + timestampSame
//                    + "\n   oldSentByUser=" + oldItem.isSentByUser()
//                    + ", newSentByUser=" + newItem.isSentByUser()
//                    + " -> " + sentByUserSame
//                    + "\n   FINAL RESULT -> " + result);
            return result;
        }

        private static boolean safeEquals(Object a, Object b) {
            if (a == null) return b == null;
            return a.equals(b);
        }
    }
}
