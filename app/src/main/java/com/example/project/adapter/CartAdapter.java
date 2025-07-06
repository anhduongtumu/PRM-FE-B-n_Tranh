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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        // Load product image from URL using Glide
        Glide.with(holder.itemView.getContext())
                .load(product.getImageURL())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.ivProductImage);

        holder.tvProductName.setText(product.getProductName());
        if (product.getCategory() != null && product.getCategory().getCategoryName() != null) {
            holder.tvProductCategory.setText(product.getCategory().getCategoryName());
        } else {
            holder.tvProductCategory.setText("Không rõ danh mục"); // hoặc ẩn view nếu bạn muốn
        }
        holder.tvProductPrice.setText(currencyFormat.format(product.getPrice()) + "đ");
        holder.tvProductOriginalPrice.setVisibility(View.GONE);

        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = cartItem.getQuantity();
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                cartItem.setQuantity(newQuantity);
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                if (listener != null) listener.onQuantityChanged(cartItem, newQuantity);
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() + 1;
            cartItem.setQuantity(newQuantity);
            holder.tvQuantity.setText(String.valueOf(newQuantity));
            if (listener != null) listener.onQuantityChanged(cartItem, newQuantity);
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onItemRemoved(cartItem);
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
