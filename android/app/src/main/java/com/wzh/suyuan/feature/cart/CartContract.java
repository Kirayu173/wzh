package com.wzh.suyuan.feature.cart;

import java.util.List;

import com.wzh.suyuan.data.db.entity.CartEntity;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface CartContract {
    interface View extends BaseView {
        void onCartLoaded(List<CartEntity> items);

        void onCartLoadFailed(String message);

        void onCartItemUpdateFailed(CartEntity item, int oldQuantity, String message);

        void onCartItemSelectionFailed(CartEntity item, boolean oldSelected, String message);

        void onCartItemDeleteFailed(CartEntity item, int position, String message);
    }
}
