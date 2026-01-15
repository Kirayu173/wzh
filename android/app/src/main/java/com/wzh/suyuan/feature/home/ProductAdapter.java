package com.wzh.suyuan.feature.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.FormatUtils;
import com.wzh.suyuan.network.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product> items = new ArrayList<>();
    private OnProductClickListener listener;

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Product> products, boolean append) {
        if (!append) {
            items.clear();
        }
        if (products != null) {
            items.addAll(products);
        }
        notifyDataSetChanged();
    }

    public List<Product> getItems() {
        return items;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = items.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView coverView;
        private final TextView nameView;
        private final TextView priceView;
        private final TextView originView;
        private final TextView stockView;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            coverView = itemView.findViewById(R.id.product_image);
            nameView = itemView.findViewById(R.id.product_name);
            priceView = itemView.findViewById(R.id.product_price);
            originView = itemView.findViewById(R.id.product_origin);
            stockView = itemView.findViewById(R.id.product_stock);
        }

        void bind(Product product, OnProductClickListener listener) {
            nameView.setText(product.getName());
            priceView.setText("¥" + FormatUtils.formatPrice(product.getPrice()));
            originView.setText(itemView.getContext().getString(R.string.label_origin_value, safeText(product.getOrigin())));
            Integer stock = product.getStock();
            stockView.setText(itemView.getContext().getString(R.string.label_stock_value, stock == null ? 0 : stock));
            Glide.with(itemView.getContext())
                    .load(product.getCoverUrl())
                    .placeholder(R.drawable.ic_launcher)
                    .into(coverView);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }

        private String safeText(String value) {
            return value == null || value.isEmpty() ? "-" : value;
        }
    }
}
