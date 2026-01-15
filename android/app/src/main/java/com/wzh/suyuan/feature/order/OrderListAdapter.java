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
import com.wzh.suyuan.network.model.OrderItem;
import com.wzh.suyuan.network.model.OrderSummary;

import java.util.ArrayList;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(OrderSummary order);
    }

    private final List<OrderSummary> items = new ArrayList<>();
    private OnOrderClickListener listener;

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<OrderSummary> orders) {
        items.clear();
        if (orders != null) {
            items.addAll(orders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_list, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderIdView;
        private final TextView statusView;
        private final ImageView imageView;
        private final TextView summaryView;
        private final TextView totalView;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdView = itemView.findViewById(R.id.order_item_id);
            statusView = itemView.findViewById(R.id.order_item_status);
            imageView = itemView.findViewById(R.id.order_item_image);
            summaryView = itemView.findViewById(R.id.order_item_summary);
            totalView = itemView.findViewById(R.id.order_item_total);
        }

        void bind(OrderSummary order, OnOrderClickListener listener) {
            if (order == null) {
                return;
            }
            orderIdView.setText(itemView.getContext()
                    .getString(R.string.order_number_value, String.valueOf(order.getId())));
            statusView.setText(OrderStatus.getLabel(itemView.getContext(), order.getStatus()));
            List<OrderItem> items = order.getItems();
            if (items != null && !items.isEmpty()) {
                OrderItem first = items.get(0);
                String name = first.getProductName() == null ? "" : first.getProductName();
                int totalCount = 0;
                for (OrderItem item : items) {
                    if (item.getQuantity() != null) {
                        totalCount += item.getQuantity();
                    }
                }
                if (name.isEmpty()) {
                    summaryView.setText(itemView.getContext()
                            .getString(R.string.order_item_summary_fallback, totalCount));
                } else {
                    summaryView.setText(itemView.getContext()
                            .getString(R.string.order_item_summary, name, totalCount));
                }
                Glide.with(itemView.getContext())
                        .load(first.getProductImage())
                        .placeholder(R.drawable.ic_launcher)
                        .into(imageView);
            } else {
                summaryView.setText(R.string.order_item_summary_empty);
                imageView.setImageResource(R.drawable.ic_launcher);
            }
            String price = itemView.getContext().getString(
                    R.string.price_value, FormatUtils.formatPrice(order.getTotalAmount()));
            totalView.setText(itemView.getContext().getString(R.string.order_total_value, price));
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(order);
                }
            });
        }
    }
}
