package com.example.project.adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.activity.ProductDetailActivity;
import com.example.project.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ProductViewHolder> {

    private List<Product> products = new ArrayList<>();

    public ProductGridAdapter(List<Product> products) {
        this.products = products;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_grid, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct, imageSale;
        TextView textName, textPrice, textOriginalPrice, textRating, textCategory;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            imageSale = itemView.findViewById(R.id.imageSale);
            textName = itemView.findViewById(R.id.textName);
            textPrice = itemView.findViewById(R.id.textPrice);
            textOriginalPrice = itemView.findViewById(R.id.textOriginalPrice);
            textRating = itemView.findViewById(R.id.textRating);
            textCategory = itemView.findViewById(R.id.textCategory);
        }

        public void bind(Product product) {
            // Set text fields first
            if (textName != null) {
                textName.setText(product.getProductName());
            }

            if (textPrice != null) {
                textPrice.setText(product.getPrice() + "đ");
            }

            if (textRating != null) {
                textRating.setText("★ " + product.getRating());
            }

            // Set category safely
            if (textCategory != null) {
                String categoryName = "Không rõ";
                if (product.getCategory() != null && product.getCategory().getCategoryName() != null) {
                    categoryName = product.getCategory().getCategoryName();
                }
                textCategory.setText(categoryName);
            }

            // Handle sale price and original price
            if (product.isOnSale() && product.getOriginalPrice() != null && !product.getOriginalPrice().isEmpty()) {
                if (imageSale != null) {
                    imageSale.setVisibility(View.VISIBLE);
                }
                if (textOriginalPrice != null) {
                    textOriginalPrice.setVisibility(View.VISIBLE);
                    textOriginalPrice.setText(product.getOriginalPrice() + "đ");
                    textOriginalPrice.setPaintFlags(textOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            } else {
                if (imageSale != null) {
                    imageSale.setVisibility(View.GONE);
                }
                if (textOriginalPrice != null) {
                    textOriginalPrice.setVisibility(View.GONE);
                }
            }

            // Load image with null check
            if (imageProduct != null) {
                Glide.with(itemView.getContext())
                        .load(product.getImageURL())
                        .placeholder(R.drawable.placeholder_image)
                        .into(imageProduct);
            }

            // Click để mở ProductDetailActivity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                intent.putExtra("name", product.getProductName());
                intent.putExtra("price", product.getPrice() + "đ");
                intent.putExtra("originalPrice", product.getOriginalPrice());
                intent.putExtra("rating", product.getRating());
                intent.putExtra("imageUrl", product.getImageURL());

                // Safe category handling
                String categoryName = "Không rõ";
                if (product.getCategory() != null && product.getCategory().getCategoryName() != null) {
                    categoryName = product.getCategory().getCategoryName();
                }
                intent.putExtra("category", categoryName);

                itemView.getContext().startActivity(intent);
            });
        }
    }
}