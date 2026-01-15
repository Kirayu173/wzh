package com.wzh.suyuan.feature.admin.order;

import com.wzh.suyuan.network.model.OrderDetail;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface AdminOrderDetailContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void showOrderDetail(OrderDetail detail);

        void showError(String message);
    }
}
