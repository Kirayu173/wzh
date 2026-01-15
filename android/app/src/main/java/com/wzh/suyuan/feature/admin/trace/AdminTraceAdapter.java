package com.wzh.suyuan.feature.admin.trace;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wzh.suyuan.R;
import com.wzh.suyuan.network.model.TraceBatch;

import java.util.ArrayList;
import java.util.List;

public class AdminTraceAdapter extends RecyclerView.Adapter<AdminTraceAdapter.TraceViewHolder> {

    public interface TraceActionListener {
        void onEdit(TraceBatch batch);

        void onQrCode(TraceBatch batch);

        void onLogistics(TraceBatch batch);

        void onDelete(TraceBatch batch);
    }

    private final List<TraceBatch> items = new ArrayList<>();
    private TraceActionListener listener;

    public void setTraceActionListener(TraceActionListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<TraceBatch> batches) {
        items.clear();
        if (batches != null) {
            items.addAll(batches);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TraceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_trace, parent, false);
        return new TraceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TraceViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TraceViewHolder extends RecyclerView.ViewHolder {
        private final TextView codeView;
        private final TextView productView;
        private final TextView originView;
        private final Button editButton;
        private final Button qrButton;
        private final Button logisticsButton;
        private final Button deleteButton;

        TraceViewHolder(@NonNull View itemView) {
            super(itemView);
            codeView = itemView.findViewById(R.id.admin_trace_code);
            productView = itemView.findViewById(R.id.admin_trace_product);
            originView = itemView.findViewById(R.id.admin_trace_origin);
            editButton = itemView.findViewById(R.id.admin_trace_action_edit);
            qrButton = itemView.findViewById(R.id.admin_trace_action_qrcode);
            logisticsButton = itemView.findViewById(R.id.admin_trace_action_logistics);
            deleteButton = itemView.findViewById(R.id.admin_trace_action_delete);
        }

        void bind(TraceBatch batch, TraceActionListener listener) {
            if (batch == null) {
                return;
            }
            codeView.setText(batch.getTraceCode());
            String product = batch.getProductName();
            productView.setText(itemView.getContext().getString(
                    R.string.trace_product_value, product == null ? "-" : product));
            originView.setText(itemView.getContext().getString(
                    R.string.trace_origin_value, batch.getOrigin() == null ? "-" : batch.getOrigin()));
            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(batch);
                }
            });
            qrButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQrCode(batch);
                }
            });
            logisticsButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLogistics(batch);
                }
            });
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(batch);
                }
            });
        }
    }
}
