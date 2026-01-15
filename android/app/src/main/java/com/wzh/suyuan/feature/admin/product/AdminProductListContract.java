package com.wzh.suyuan.feature.admin.product;

import java.util.List;

import com.wzh.suyuan.network.model.Product;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface AdminProductListContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void showProducts(List<Product> products);

        void showEmpty(String message);

        void showError(String message);
    }
}
