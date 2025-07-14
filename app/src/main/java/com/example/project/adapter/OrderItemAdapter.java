package com.example.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.model.CartItem;
import com.example.project.model.OrderItem;
import com.example.project.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<OrderItem> orderItems;
    private NumberFormat currencyFormat;

    public OrderItemAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        this.currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_confirmation, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    public void updateItems(List<OrderItem> newItems) {
        this.orderItems = newItems;
        notifyDataSetChanged();
    }

    class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName;
        private TextView tvProductPrice;
        private TextView tvQuantity;
        private TextView tvItemTotal;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
        }

        public void bind(OrderItem item) {
            Product product = item.getProduct();

            // Load image from URL using Glide
            Glide.with(itemView.getContext())
                    .load(product.getImageURL())
                    .placeholder(R.drawable.placeholder_image)
                    .into(ivProductImage);

            tvProductName.setText(product.getProductName());
            tvProductPrice.setText(formatPrice(product.getPrice()));
            tvQuantity.setText("x" + item.getQuantity());

            double itemTotal = product.getPrice() * item.getQuantity();
            tvItemTotal.setText(formatPrice(itemTotal));
        }

        private String formatPrice(double price) {
            return currencyFormat.format(price) + "đ";
        }
    }
}
