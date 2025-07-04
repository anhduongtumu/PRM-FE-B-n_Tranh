package com.example.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.model.CartItem;
import com.example.project.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<CartItem> orderItems;
    private NumberFormat currencyFormat;

    public OrderItemAdapter(List<CartItem> orderItems) {
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
        CartItem item = orderItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    public void updateItems(List<CartItem> newItems) {
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
            initViews();
        }

        private void initViews() {
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvItemTotal = itemView.findViewById(R.id.tvItemTotal);
        }

        public void bind(CartItem item) {
            Product product = item.getProduct();

            // Set product image
            ivProductImage.setImageResource(product.getImageRes());

            // Set product name
            tvProductName.setText(product.getName());

            // Set product price
            tvProductPrice.setText(product.getPrice());

            // Set quantity
            tvQuantity.setText("x" + item.getQuantity());

            // Calculate and set item total
            double price = parsePrice(product.getPrice());
            double itemTotal = price * item.getQuantity();
            tvItemTotal.setText(formatPrice(itemTotal));
        }

        private double parsePrice(String priceString) {
            // Remove "đ" and "." from price string and convert to double
            return Double.parseDouble(priceString.replaceAll("[đ.,]", ""));
        }

        private String formatPrice(double price) {
            return currencyFormat.format(price) + "đ";
        }
    }
}
