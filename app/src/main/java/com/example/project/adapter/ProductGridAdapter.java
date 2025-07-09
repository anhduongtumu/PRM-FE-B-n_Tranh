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

    class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct, imageSale;
        TextView textName, textPrice, textOriginalPrice, textRating, textCategory;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.ivProductImage);
            imageSale = itemView.findViewById(R.id.imageSale);
            textName = itemView.findViewById(R.id.tvProductName);
            textPrice = itemView.findViewById(R.id.tvProductPrice);
            textOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            textRating = itemView.findViewById(R.id.tvRating);
            textCategory = itemView.findViewById(R.id.tvCategory);
        }

        public void bind(Product product) {
            Glide.with(itemView.getContext())
                    .load(product.getImageURL())
                    .placeholder(R.drawable.placeholder_image)
                    .into(imageProduct);

            textName.setText(product.getProductName());
            textPrice.setText(product.getPrice() + "đ");
            textRating.setText("★ " + product.getRating());

            // Lấy tên danh mục an toàn
            String categoryName = "Không rõ";
            if (product.getCategory() != null && product.getCategory().getCategoryName() != null) {
                categoryName = product.getCategory().getCategoryName();
            }
            textCategory.setText(categoryName);

            if (product.isOnSale() && product.getOriginalPrice() != null && !product.getOriginalPrice().isEmpty()) {
                imageSale.setVisibility(View.VISIBLE);
                textOriginalPrice.setVisibility(View.VISIBLE);
                textOriginalPrice.setText(product.getOriginalPrice() + "đ");
                textOriginalPrice.setPaintFlags(textOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                imageSale.setVisibility(View.GONE);
                textOriginalPrice.setVisibility(View.GONE);
            }

            // Click để mở ProductDetailActivity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                intent.putExtra("name", product.getProductName());
                intent.putExtra("price", product.getPrice() + "đ");
                intent.putExtra("originalPrice", product.getOriginalPrice());
                intent.putExtra("rating", product.getRating());
                intent.putExtra("imageUrl", product.getImageURL()); // dùng URL thay vì res
                intent.putExtra("category", product.getCategory().getCategoryName());
                itemView.getContext().startActivity(intent);
            });
        }
    }
}