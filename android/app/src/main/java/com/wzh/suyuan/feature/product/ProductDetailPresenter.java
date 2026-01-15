package com.wzh.suyuan.feature.product;

import android.content.Context;
import android.util.Log;

import com.wzh.suyuan.AppExecutors;
import com.wzh.suyuan.data.auth.AuthManager;
import com.wzh.suyuan.data.cart.CartMapper;
import com.wzh.suyuan.data.db.AppDatabase;
import com.wzh.suyuan.data.db.entity.CartEntity;
import com.wzh.suyuan.data.db.entity.ProductEntity;
import com.wzh.suyuan.data.product.ProductMapper;
import com.wzh.suyuan.kit.NetworkUtils;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.CartAddRequest;
import com.wzh.suyuan.network.model.CartAddResponse;
import com.wzh.suyuan.network.model.Product;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailPresenter extends BasePresenter<ProductDetailContract.View> {
    private static final String TAG = "ProductDetailPresenter";

    public void loadProduct(Context context, long productId) {
        if (context == null) {
            return;
        }
        ApiClient.getService().getProductDetail(productId)
                .enqueue(new Callback<BaseResponse<Product>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Product>> call,
                                           Response<BaseResponse<Product>> response) {
                        ProductDetailContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        view.showLoading(false);
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "detail failed http=" + response.code());
                            view.showError("商品详情加载失败");
                            loadCachedProduct(context, productId);
                            return;
                        }
                        BaseResponse<Product> body = response.body();
                        if (!body.isSuccess() || body.getData() == null) {
                            view.showError(body.getMessage());
                            loadCachedProduct(context, productId);
                            return;
                        }
                        Product product = body.getData();
                        view.showProduct(product);
                        cacheProduct(context, product);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Product>> call, Throwable t) {
                        ProductDetailContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        Log.w(TAG, "detail error", t);
                        view.showLoading(false);
                        view.showError("网络异常，请稍后重试");
                        loadCachedProduct(context, productId);
                    }
                });
    }

    public void addToCart(Context context, Product product, int quantity) {
        if (context == null || product == null || product.getId() == null) {
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(context)) {
            saveLocalCart(context, product, quantity);
            return;
        }
        ApiClient.getService().addToCart(new CartAddRequest(product.getId(), quantity))
                .enqueue(new Callback<BaseResponse<CartAddResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<CartAddResponse>> call,
                                           Response<BaseResponse<CartAddResponse>> response) {
                        ProductDetailContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "add cart failed http=" + response.code());
                            saveLocalCart(context, product, quantity);
                            return;
                        }
                        BaseResponse<CartAddResponse> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(body.getMessage());
                            return;
                        }
                        view.onAddToCartSuccess(false);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<CartAddResponse>> call, Throwable t) {
                        Log.w(TAG, "add cart error", t);
                        saveLocalCart(context, product, quantity);
                    }
                });
    }

    private void cacheProduct(Context context, Product product) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            ProductEntity entity = ProductMapper.toEntity(product);
            if (entity != null) {
                AppDatabase.getInstance(context).productDao().insert(entity);
            }
        });
    }

    private void loadCachedProduct(Context context, long productId) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            ProductEntity entity = AppDatabase.getInstance(context).productDao().getById(productId);
            Product cached = ProductMapper.toModel(entity);
            AppExecutors.getInstance().mainThread().execute(() -> {
                ProductDetailContract.View view = getView();
                if (view != null && cached != null) {
                    view.showCachedProduct(cached);
                }
            });
        });
    }

    private void saveLocalCart(Context context, Product product, int quantity) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            long userId = getUserId(context);
            CartEntity entity = CartMapper.toLocalEntity(userId, product, quantity);
            AppDatabase.getInstance(context).cartDao().insert(entity);
            AppExecutors.getInstance().mainThread().execute(() -> {
                ProductDetailContract.View view = getView();
                if (view != null) {
                    view.onAddToCartSuccess(true);
                }
            });
        });
    }

    private long getUserId(Context context) {
        if (context == null) {
            return 0L;
        }
        if (AuthManager.getUser(context) != null && AuthManager.getUser(context).getId() != null) {
            return AuthManager.getUser(context).getId();
        }
        return 0L;
    }
}
