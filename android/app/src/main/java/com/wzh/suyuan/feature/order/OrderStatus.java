package com.wzh.suyuan.feature.order;

import android.content.Context;

import com.wzh.suyuan.R;

public final class OrderStatus {
    public static final String PENDING_PAY = "PENDING_PAY";
    public static final String PAID = "PAID";
    public static final String SHIPPED = "SHIPPED";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELED = "CANCELED";

    private OrderStatus() {
    }

    public static String getLabel(Context context, String status) {
        if (context == null) {
            return status == null ? "" : status;
        }
        if (PENDING_PAY.equals(status)) {
            return context.getString(R.string.order_status_pending);
        }
        if (PAID.equals(status)) {
            return context.getString(R.string.order_status_paid);
        }
        if (SHIPPED.equals(status)) {
            return context.getString(R.string.order_status_shipped);
        }
        if (COMPLETED.equals(status)) {
            return context.getString(R.string.order_status_completed);
        }
        if (CANCELED.equals(status)) {
            return context.getString(R.string.order_status_canceled);
        }
        return status == null ? "" : status;
    }
}
