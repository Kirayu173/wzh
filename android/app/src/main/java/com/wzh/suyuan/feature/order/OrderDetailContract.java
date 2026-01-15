package com.wzh.suyuan.feature.order;

import com.wzh.suyuan.network.model.OrderDetail;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface OrderDetailContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void showOrder(OrderDetail detail);
    }
}
