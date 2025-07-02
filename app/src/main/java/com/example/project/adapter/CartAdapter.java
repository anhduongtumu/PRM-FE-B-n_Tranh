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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemListener listener;
    private NumberFormat currencyFormat;

    public interface OnCartItemListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemRemoved(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, OnCartItemListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        // Set product image
        holder.ivProductImage.setImageResource(product.getImageRes());

        // Set product name
        holder.tvProductName.setText(product.getName());

        // Set product category
        holder.tvProductCategory.setText(product.getCategory());

        // Set product price
        holder.tvProductPrice.setText(product.getPrice());

        // Set original price if available
        if (product.getOriginalPrice() != null && !product.getOriginalPrice().isEmpty()) {
            holder.tvProductOriginalPrice.setText(product.getOriginalPrice());
            holder.tvProductOriginalPrice.setVisibility(View.VISIBLE);
            // Add strikethrough effect
            holder.tvProductOriginalPrice.setPaintFlags(
                    holder.tvProductOriginalPrice.getPaintFlags() |
                            android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            );
        } else {
            holder.tvProductOriginalPrice.setVisibility(View.GONE);
        }

        // Set quantity
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

        // Set click listeners
        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = cartItem.getQuantity();
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                cartItem.setQuantity(newQuantity);
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                if (listener != null) {
                    listener.onQuantityChanged(cartItem, newQuantity);
                }
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int currentQuantity = cartItem.getQuantity();
            int newQuantity = currentQuantity + 1;
            cartItem.setQuantity(newQuantity);
            holder.tvQuantity.setText(String.valueOf(newQuantity));
            if (listener != null) {
                listener.onQuantityChanged(cartItem, newQuantity);
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemRemoved(cartItem);
            }
        });

        // Handle long press for quantity input
        holder.tvQuantity.setOnLongClickListener(v -> {
            // You can implement a dialog for direct quantity input here
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
        }
    }

    public void updateItemQuantity(int position, int newQuantity) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.get(position).setQuantity(newQuantity);
            notifyItemChanged(position);
        }
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvProductCategory;
        TextView tvProductPrice;
        TextView tvProductOriginalPrice;
        TextView tvQuantity;
        ImageView btnDecrease;
        ImageView btnIncrease;
        ImageView btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductOriginalPrice = itemView.findViewById(R.id.tvProductOriginalPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
