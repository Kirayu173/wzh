package com.wzh.suyuan.feature.trace;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.wzh.suyuan.R;
import com.wzh.suyuan.data.db.entity.ScanRecordEntity;

public class TraceRecordAdapter extends RecyclerView.Adapter<TraceRecordAdapter.TraceRecordViewHolder> {
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    private final List<ScanRecordEntity> items = new ArrayList<>();
    private OnRecordClickListener listener;

    public interface OnRecordClickListener {
        void onRecordClick(ScanRecordEntity record);
    }

    public void setOnRecordClickListener(OnRecordClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<ScanRecordEntity> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TraceRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trace_record, parent, false);
        return new TraceRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TraceRecordViewHolder holder, int position) {
        ScanRecordEntity record = items.get(position);
        holder.codeView.setText(safeText(record == null ? null : record.getTraceCode()));
        holder.productView.setText(safeText(record == null ? null : record.getProductName()));
        holder.timeView.setText(formatTime(record == null ? 0 : record.getScanTime()));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && record != null) {
                listener.onRecordClick(record);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TraceRecordViewHolder extends RecyclerView.ViewHolder {
        private final TextView codeView;
        private final TextView productView;
        private final TextView timeView;

        TraceRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            codeView = itemView.findViewById(R.id.trace_record_code);
            productView = itemView.findViewById(R.id.trace_record_product);
            timeView = itemView.findViewById(R.id.trace_record_time);
        }
    }

    private String safeText(String value) {
        return TextUtils.isEmpty(value) ? "-" : value;
    }

    private String formatTime(long time) {
        if (time <= 0) {
            return "--";
        }
        return TIME_FORMAT.format(new Date(time));
    }
}
