package com.example.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import com.example.project.activity.OrderConfirmationActivity;
import com.example.project.model.Order;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final List<Order> orders;
    private final Context context;

    public OrderHistoryAdapter(List<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.tvOrderId.setText("Mã đơn: #" + order.getId());
        holder.tvOrderDate.setText("Ngày đặt: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(order.getOrderDate()));
        holder.tvStatus.setText("Trạng thái: " + order.getOrderStatus());
        holder.tvTotalAmount.setText("Tổng tiền: " + String.format(Locale.getDefault(), "%,.0fđ", order.getTotal()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderConfirmationActivity.class);
            intent.putExtra("orderId", order.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvStatus, tvTotalAmount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
        }
    }
}

