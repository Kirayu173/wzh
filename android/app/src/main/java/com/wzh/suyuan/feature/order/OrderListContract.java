package com.wzh.suyuan.feature.order;

import java.util.List;

import com.wzh.suyuan.network.model.OrderSummary;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface OrderListContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void showOrders(List<OrderSummary> orders, int page, int size, long total);

        void showCachedOrders(List<OrderSummary> orders);

        void showEmpty(String message);
    }
}
