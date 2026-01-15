package com.wzh.suyuan.feature.home;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.wzh.suyuan.AppExecutors;
import com.wzh.suyuan.data.db.AppDatabase;
import com.wzh.suyuan.data.db.entity.ProductEntity;
import com.wzh.suyuan.data.product.ProductMapper;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.Product;
import com.wzh.suyuan.network.model.ProductPage;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePresenter extends BasePresenter<HomeContract.View> {
    private static final String TAG = "HomePresenter";

    public void loadProducts(Context context, int page, int size, boolean isRefresh) {
        ApiClient.getService().getProducts(page, size)
                .enqueue(new Callback<BaseResponse<ProductPage>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<ProductPage>> call,
                                           Response<BaseResponse<ProductPage>> response) {
                        HomeContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "products failed http=" + response.code());
                            view.onProductsLoadFailed("商品加载失败", isRefresh);
                            loadCachedProducts(context, page, size);
                            return;
                        }
                        BaseResponse<ProductPage> body = response.body();
                        if (!body.isSuccess() || body.getData() == null) {
                            view.onProductsLoadFailed(body.getMessage(), isRefresh);
                            loadCachedProducts(context, page, size);
                            return;
                        }
                        ProductPage pageData = body.getData();
                        List<Product> items = pageData.getItems();
                        boolean hasMore = page * size < pageData.getTotal();
                        view.onProductsLoaded(items == null ? new ArrayList<>() : items, isRefresh, hasMore);
                        cacheProducts(context, items);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<ProductPage>> call, Throwable t) {
                        HomeContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        Log.w(TAG, "products error", t);
                        view.onProductsLoadFailed("网络异常，请稍后重试", isRefresh);
                        loadCachedProducts(context, page, size);
                    }
                });
    }

    private void cacheProducts(Context context, List<Product> products) {
        if (context == null || products == null || products.isEmpty()) {
            return;
        }
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<ProductEntity> entities = new ArrayList<>();
            for (Product product : products) {
                ProductEntity entity = ProductMapper.toEntity(product);
                if (entity != null) {
                    entities.add(entity);
                }
            }
            if (!entities.isEmpty()) {
                AppDatabase.getInstance(context).productDao().insertAll(entities);
            }
        });
    }

    private void loadCachedProducts(Context context, int page, int size) {
        if (context == null) {
            return;
        }
        AppExecutors.getInstance().diskIO().execute(() -> {
            int offset = Math.max(page - 1, 0) * size;
            List<ProductEntity> entities = AppDatabase.getInstance(context)
                    .productDao()
                    .getPage(size, offset);
            List<Product> cached = new ArrayList<>();
            for (ProductEntity entity : entities) {
                Product model = ProductMapper.toModel(entity);
                if (model != null) {
                    cached.add(model);
                }
            }
            AppExecutors.getInstance().mainThread().execute(() -> {
                HomeContract.View view = getView();
                if (view != null && !cached.isEmpty()) {
                    view.onCachedProductsLoaded(cached);
                }
            });
        });
    }
}
