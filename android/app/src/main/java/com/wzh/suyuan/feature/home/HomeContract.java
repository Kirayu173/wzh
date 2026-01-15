package com.wzh.suyuan.feature.home;

import java.util.List;

import com.wzh.suyuan.network.model.Product;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface HomeContract {
    interface View extends BaseView {
        void onProductsLoaded(List<Product> products, boolean isRefresh, boolean hasMore);

        void onProductsLoadFailed(String message, boolean isRefresh);

        void onCachedProductsLoaded(List<Product> products);
    }
}
