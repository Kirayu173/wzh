package com.wzh.suyuan.feature.cart;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wzh.suyuan.R;
import com.wzh.suyuan.data.db.entity.CartEntity;
import com.wzh.suyuan.kit.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface CartActionListener {
        void onQuantityChanged(CartEntity item, int newQuantity);

        void onSelectionChanged(CartEntity item, boolean selected);

        void onDelete(CartEntity item, int position);
    }

    private final List<CartEntity> items = new ArrayList<>();
    private CartActionListener listener;

    public void setCartActionListener(CartActionListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<CartEntity> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<CartEntity> getItems() {
        return items;
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
        CartEntity item = items.get(position);
        holder.bind(item, listener, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox selectBox;
        private final ImageView coverView;
        private final TextView nameView;
        private final TextView priceView;
        private final TextView quantityView;
        private final TextView minusView;
        private final TextView plusView;
        private final TextView deleteView;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            selectBox = itemView.findViewById(R.id.cart_item_check);
            coverView = itemView.findViewById(R.id.cart_item_image);
            nameView = itemView.findViewById(R.id.cart_item_name);
            priceView = itemView.findViewById(R.id.cart_item_price);
            quantityView = itemView.findViewById(R.id.cart_item_quantity);
            minusView = itemView.findViewById(R.id.cart_item_minus);
            plusView = itemView.findViewById(R.id.cart_item_plus);
            deleteView = itemView.findViewById(R.id.cart_item_delete);
        }

        void bind(CartEntity item, CartActionListener listener, int position) {
            nameView.setText(item.getProductName());
            priceView.setText(itemView.getContext().getString(
                    R.string.price_value, FormatUtils.formatPrice(item.getPriceSnapshot())));
            quantityView.setText(String.valueOf(item.getQuantity()));
            Glide.with(itemView.getContext())
                    .load(item.getProductImage())
                    .placeholder(R.drawable.ic_launcher)
                    .into(coverView);

            selectBox.setOnCheckedChangeListener(null);
            selectBox.setChecked(item.isSelected());
            selectBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onSelectionChanged(item, isChecked);
                }
            });

            minusView.setOnClickListener(v -> {
                if (listener != null && item.getQuantity() > 1) {
                    listener.onQuantityChanged(item, item.getQuantity() - 1);
                }
            });
            plusView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityChanged(item, item.getQuantity() + 1);
                }
            });
            deleteView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(item, position);
                }
            });
        }
    }
}
