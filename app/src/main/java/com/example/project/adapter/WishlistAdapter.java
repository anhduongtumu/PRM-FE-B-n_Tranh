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
import com.example.project.model.Product;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<Product> products;
    private OnWishlistItemClickListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    public interface OnWishlistItemClickListener {
        void onItemClick(Product product);
        void onRemoveFromWishlist(Product product, int position);
        void onAddToCart(Product product);
    }

    public WishlistAdapter(List<Product> products) {
        this.products = products;
    }

    public void setOnWishlistItemClickListener(OnWishlistItemClickListener listener) {
        this.listener = listener;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wishlist, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, position);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    class WishlistViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView imgProduct;
        private final TextView tvProductName, tvPrice, tvOriginalPrice, tvDiscount, tvRating, tvCategory;
        private final ImageView btnRemoveWishlist;
        private final MaterialButton btnAddToCart;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardWishlistItem);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnRemoveWishlist = itemView.findViewById(R.id.btnRemoveWishlist);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }

        public void bind(Product product, int position) {
            // Load image from URL using Glide
            Glide.with(itemView.getContext())
                    .load(product.getImageURL())
                    .placeholder(R.drawable.placeholder_image)
                    .into(imgProduct);

            // Set product name
            tvProductName.setText(product.getProductName());

            // Giá hiện tại
            double currentPrice = product.getPrice();
            tvPrice.setText(formatPrice(currentPrice));

            // Giá gốc và giảm giá (nếu có)
            double originalPrice = parsePrice(product.getOriginalPrice()); // ✔ Đúng
            if (originalPrice > currentPrice) {
                tvOriginalPrice.setVisibility(View.VISIBLE);
                tvOriginalPrice.setText(formatPrice(originalPrice));

                int discountPercent = (int) ((originalPrice - currentPrice) / originalPrice * 100);
                tvDiscount.setVisibility(View.VISIBLE);
                tvDiscount.setText("-" + discountPercent + "%");
            } else {
                tvOriginalPrice.setVisibility(View.GONE);
                tvDiscount.setVisibility(View.GONE);
            }

            // Rating & category
            tvRating.setText(String.valueOf(product.getRating()));
            tvCategory.setText(
                    product.getCategory() != null ? product.getCategory().toString() : "Đang cập nhật"
            );

            // Click listeners
            cardView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(product);
            });

            btnRemoveWishlist.setOnClickListener(v -> {
                if (listener != null) listener.onRemoveFromWishlist(product, position);
            });

            btnAddToCart.setOnClickListener(v -> {
                if (listener != null) listener.onAddToCart(product);
            });
        }

        private double parsePrice(String priceString) {
            if (priceString == null || priceString.isEmpty()) return 0;
            try {
                return Double.parseDouble(priceString.replaceAll("[^\\d]", ""));
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        private String formatPrice(double price) {
            return currencyFormat.format(price) + "đ";
        }

    }
}
