package com.wzh.suyuan.feature.admin.order;

import java.util.List;

import com.wzh.suyuan.network.model.OrderSummary;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface AdminOrderListContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void showOrders(List<OrderSummary> orders);

        void showEmpty(String message);

        void showError(String message);
    }
}
