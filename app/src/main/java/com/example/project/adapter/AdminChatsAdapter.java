package com.example.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import com.example.project.model.Chat;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminChatsAdapter extends RecyclerView.Adapter<AdminChatsAdapter.ChatViewHolder> {

    private List<Chat> chatList = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy, h:mm a", Locale.getDefault());

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    private final OnChatClickListener listener;

    public AdminChatsAdapter(OnChatClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Chat> newList) {
        chatList.clear();
        chatList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_preview, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.bind(chat);
        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCustomerName, textViewLastMessage, textViewTimestamp;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCustomerName = itemView.findViewById(R.id.textViewCustomerName);
            textViewLastMessage = itemView.findViewById(R.id.textViewLastMessage);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
        }

        void bind(Chat chat) {
            // Replace with customer name resolution if needed
            textViewCustomerName.setText(chat.getCustomerName());
            textViewLastMessage.setText(chat.getLastMessage());
            Timestamp ts = chat.getLastMessageTimestamp();
            String formatted = ts != null ? formatTimestamp(ts) : "N/A";
            textViewTimestamp.setText(formatted);
        }

        private String formatTimestamp(Timestamp ts) {
            if (ts == null) return "N/A";

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
    }
}
