package com.wzh.suyuan.feature.admin.product;

import com.wzh.suyuan.ui.mvp.BaseView;

public interface AdminProductEditContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void showProduct(com.wzh.suyuan.network.model.Product product);

        void onSaveSuccess();

        void showError(String message);
    }
}
