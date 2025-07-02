package com.example.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import com.example.project.model.Product;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.DecimalFormat;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<Product> products;
    private OnWishlistItemClickListener listener;

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
        return products.size();
    }

    class WishlistViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView cardView;
        private ImageView imgProduct;
        private TextView tvProductName;
        private TextView tvPrice;
        private TextView tvOriginalPrice;
        private TextView tvDiscount;
        private TextView tvRating;
        private TextView tvCategory;
        private ImageView btnRemoveWishlist;
        private MaterialButton btnAddToCart;

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
            // Set product image
            imgProduct.setImageResource(product.getImageRes());

            // Set product name
            tvProductName.setText(product.getName());

            // Format and set prices
//            DecimalFormat formatter = new DecimalFormat("#,###");
//            tvPrice.setText(formatter.format(product.getPrice()) + "đ");
//
//            if (product.getOriginalPrice() > product.getPrice()) {
//                tvOriginalPrice.setVisibility(View.VISIBLE);
//                tvOriginalPrice.setText(formatter.format(product.getOriginalPrice()) + "đ");
//
//                // Calculate and show discount percentage
//                int discountPercent = (int) (((product.getOriginalPrice() - product.getPrice())
//                        / (float) product.getOriginalPrice()) * 100);
//                tvDiscount.setVisibility(View.VISIBLE);
//                tvDiscount.setText("-" + discountPercent + "%");
//            } else {
//                tvOriginalPrice.setVisibility(View.GONE);
//                tvDiscount.setVisibility(View.GONE);
//            }

            // Set rating
            tvRating.setText(String.valueOf(product.getRating()));

            // Set category
            tvCategory.setText(product.getCategory());

            // Set click listeners
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(product);
                }
            });

            btnRemoveWishlist.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveFromWishlist(product, position);
                }
            });

            btnAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCart(product);
                }
            });
        }
    }
}
