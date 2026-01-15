package com.wzh.suyuan.feature.product;

import com.wzh.suyuan.network.model.Product;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface ProductDetailContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void showProduct(Product product);

        void showCachedProduct(Product product);

        void showError(String message);

        void onAddToCartSuccess(boolean isLocal);
    }
}
