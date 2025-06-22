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
import java.util.List;

public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ProductViewHolder> {

    private List<Product> products;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductGridAdapter(List<Product> products) {
        this.products = products;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
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
        private ImageView imageProduct, imageSale;
        private TextView textName, textPrice, textOriginalPrice, textRating, textCategory;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            imageSale = itemView.findViewById(R.id.imageSale);
            textName = itemView.findViewById(R.id.textName);
            textPrice = itemView.findViewById(R.id.textPrice);
            textOriginalPrice = itemView.findViewById(R.id.textOriginalPrice);
            textRating = itemView.findViewById(R.id.textRating);
            textCategory = itemView.findViewById(R.id.textCategory);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onProductClick(products.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Product product) {
            imageProduct.setImageResource(product.getImageRes());
            textName.setText(product.getName());
            textPrice.setText(product.getPrice());
            textRating.setText(String.valueOf(product.getRating()));
            textCategory.setText(product.getCategory());

            if (product.isOnSale() && product.getOriginalPrice() != null) {
                imageSale.setVisibility(View.VISIBLE);
                textOriginalPrice.setVisibility(View.VISIBLE);
                textOriginalPrice.setText(product.getOriginalPrice());
            } else {
                imageSale.setVisibility(View.GONE);
                textOriginalPrice.setVisibility(View.GONE);
            }
        }
    }
}