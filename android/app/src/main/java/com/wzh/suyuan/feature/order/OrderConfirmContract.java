package com.wzh.suyuan.feature.order;

import java.util.List;

import com.wzh.suyuan.network.model.Address;
import com.wzh.suyuan.network.model.CartItem;
import com.wzh.suyuan.network.model.OrderCreateResponse;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface OrderConfirmContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void showAddress(Address address);

        void showCartItems(List<CartItem> items);

        void showEmpty(String message);

        void onOrderCreated(OrderCreateResponse response);
    }
}
