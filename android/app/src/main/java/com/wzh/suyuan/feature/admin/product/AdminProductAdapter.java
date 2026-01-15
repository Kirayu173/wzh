package com.wzh.suyuan.feature.admin.product;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.FormatUtils;
import com.wzh.suyuan.network.model.Product;

import java.util.ArrayList;
import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    public interface ProductActionListener {
        void onEdit(Product product);

        void onToggleStatus(Product product);

        void onUpdateStock(Product product);

        void onDelete(Product product);
    }

    private final List<Product> items = new ArrayList<>();
    private ProductActionListener listener;

    public void setProductActionListener(ProductActionListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<Product> products) {
        items.clear();
        if (products != null) {
            items.addAll(products);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;
        private final TextView statusView;
        private final TextView priceView;
        private final TextView stockView;
        private final Button editButton;
        private final Button statusButton;
        private final Button stockButton;
        private final Button deleteButton;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.admin_product_name);
            statusView = itemView.findViewById(R.id.admin_product_status);
            priceView = itemView.findViewById(R.id.admin_product_price);
            stockView = itemView.findViewById(R.id.admin_product_stock);
            editButton = itemView.findViewById(R.id.admin_product_action_edit);
            statusButton = itemView.findViewById(R.id.admin_product_action_status);
            stockButton = itemView.findViewById(R.id.admin_product_action_stock);
            deleteButton = itemView.findViewById(R.id.admin_product_action_delete);
        }

        void bind(Product product, ProductActionListener listener) {
            if (product == null) {
                return;
            }
            nameView.setText(product.getName());
            String status = product.getStatus();
            boolean online = "online".equalsIgnoreCase(status);
            String statusLabel = itemView.getContext().getString(
                    online ? R.string.admin_product_status_online : R.string.admin_product_status_offline);
            statusView.setText(itemView.getContext().getString(
                    R.string.admin_product_status_label) + ":" + statusLabel);
            String price = itemView.getContext().getString(
                    R.string.price_value, FormatUtils.formatPrice(product.getPrice()));
            priceView.setText(price);
            int stockValue = product.getStock() == null ? 0 : product.getStock();
            stockView.setText(itemView.getContext().getString(
                    R.string.label_stock_value, stockValue));
            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(product);
                }
            });
            statusButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onToggleStatus(product);
                }
            });
            stockButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUpdateStock(product);
                }
            });
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(product);
                }
            });
        }
    }
}
