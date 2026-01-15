package com.wzh.suyuan.feature.admin.order;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wzh.suyuan.R;
import com.wzh.suyuan.feature.order.OrderStatus;
import com.wzh.suyuan.kit.FormatUtils;
import com.wzh.suyuan.network.model.OrderItem;
import com.wzh.suyuan.network.model.OrderSummary;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderListAdapter extends RecyclerView.Adapter<AdminOrderListAdapter.OrderViewHolder> {

    public interface OrderActionListener {
        void onDetail(OrderSummary order);

        void onShip(OrderSummary order);
    }

    private final List<OrderSummary> items = new ArrayList<>();
    private OrderActionListener listener;

    public void setOrderActionListener(OrderActionListener listener) {
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
                .inflate(R.layout.item_admin_order, parent, false);
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
        private final TextView idView;
        private final TextView statusView;
        private final TextView summaryView;
        private final TextView totalView;
        private final Button detailButton;
        private final Button shipButton;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            idView = itemView.findViewById(R.id.admin_order_item_id);
            statusView = itemView.findViewById(R.id.admin_order_item_status);
            summaryView = itemView.findViewById(R.id.admin_order_item_summary);
            totalView = itemView.findViewById(R.id.admin_order_item_total);
            detailButton = itemView.findViewById(R.id.admin_order_action_detail);
            shipButton = itemView.findViewById(R.id.admin_order_action_ship);
        }

        void bind(OrderSummary order, OrderActionListener listener) {
            if (order == null) {
                return;
            }
            idView.setText(itemView.getContext()
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
            } else {
                summaryView.setText(R.string.order_item_summary_empty);
            }
            String price = itemView.getContext().getString(
                    R.string.price_value, FormatUtils.formatPrice(order.getTotalAmount()));
            totalView.setText(itemView.getContext().getString(R.string.order_total_value, price));
            detailButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDetail(order);
                }
            });
            boolean canShip = "PAID".equalsIgnoreCase(order.getStatus());
            shipButton.setVisibility(canShip ? View.VISIBLE : View.GONE);
            shipButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onShip(order);
                }
            });
        }
    }
}
