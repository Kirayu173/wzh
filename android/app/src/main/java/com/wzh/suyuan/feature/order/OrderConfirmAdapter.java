package com.wzh.suyuan.feature.order;

import android.annotation.SuppressLint;
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
import com.wzh.suyuan.network.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class OrderConfirmAdapter extends RecyclerView.Adapter<OrderConfirmAdapter.ItemViewHolder> {

    private final List<CartItem> items = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<CartItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<CartItem> getItems() {
        return items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_confirm, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameView;
        private final TextView priceView;
        private final TextView quantityView;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.order_item_image);
            nameView = itemView.findViewById(R.id.order_item_name);
            priceView = itemView.findViewById(R.id.order_item_price);
            quantityView = itemView.findViewById(R.id.order_item_quantity);
        }

        void bind(CartItem item) {
            if (item == null) {
                return;
            }
            nameView.setText(item.getProductName());
            priceView.setText(itemView.getContext().getString(
                    R.string.price_value, FormatUtils.formatPrice(item.getPriceSnapshot())));
            quantityView.setText(itemView.getContext().getString(R.string.label_quantity_value,
                    item.getQuantity() == null ? 0 : item.getQuantity()));
            Glide.with(itemView.getContext())
                    .load(item.getProductImage())
                    .placeholder(R.drawable.ic_launcher)
                    .into(imageView);
        }
    }
}
