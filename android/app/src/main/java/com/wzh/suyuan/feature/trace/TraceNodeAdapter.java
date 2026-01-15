package com.wzh.suyuan.feature.trace;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.wzh.suyuan.R;
import com.wzh.suyuan.network.model.TraceLogisticsNode;

public class TraceNodeAdapter extends RecyclerView.Adapter<TraceNodeAdapter.TraceNodeViewHolder> {
    private final List<TraceLogisticsNode> items = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<TraceLogisticsNode> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TraceNodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trace_node, parent, false);
        return new TraceNodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TraceNodeViewHolder holder, int position) {
        TraceLogisticsNode node = items.get(position);
        holder.timeView.setText(formatTime(node == null ? null : node.getNodeTime()));
        holder.locationView.setText(safeText(node == null ? null : node.getLocation()));
        holder.statusView.setText(safeText(node == null ? null : node.getStatusDesc()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TraceNodeViewHolder extends RecyclerView.ViewHolder {
        private final TextView timeView;
        private final TextView locationView;
        private final TextView statusView;

        TraceNodeViewHolder(@NonNull View itemView) {
            super(itemView);
            timeView = itemView.findViewById(R.id.trace_node_time);
            locationView = itemView.findViewById(R.id.trace_node_location);
            statusView = itemView.findViewById(R.id.trace_node_status);
        }
    }

    private String safeText(String value) {
        return TextUtils.isEmpty(value) ? "-" : value;
    }

    private String formatTime(String value) {
        if (TextUtils.isEmpty(value)) {
            return "--";
        }
        return value.replace('T', ' ');
    }
}
