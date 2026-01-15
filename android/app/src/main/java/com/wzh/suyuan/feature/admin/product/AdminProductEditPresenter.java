package com.wzh.suyuan.feature.admin.product;

import android.content.Context;
import android.util.Log;

import com.wzh.suyuan.R;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.AdminProductRequest;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.Product;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductEditPresenter extends BasePresenter<AdminProductEditContract.View> {
    private static final String TAG = "AdminProductEdit";

    public void saveProduct(Context context, long productId, AdminProductRequest request) {
        if (context == null || request == null) {
            return;
        }
        AdminProductEditContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        Call<BaseResponse<?>> call;
        if (productId > 0) {
            call = ApiClient.getService().updateAdminProduct(productId, request);
        } else {
            call = ApiClient.getService().createAdminProduct(request);
        }
        call.enqueue(new Callback<BaseResponse<?>>() {
            @Override
            public void onResponse(Call<BaseResponse<?>> call, Response<BaseResponse<?>> response) {
                AdminProductEditContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "admin product save failed http=" + response.code());
                    view.showError(context.getString(R.string.admin_product_save_failed));
                    return;
                }
                BaseResponse<?> body = response.body();
                if (!body.isSuccess()) {
                    view.showError(body.getMessage());
                    return;
                }
                view.onSaveSuccess();
            }

            @Override
            public void onFailure(Call<BaseResponse<?>> call, Throwable t) {
                AdminProductEditContract.View view = getView();
                if (view == null) {
                    return;
                }
                Log.w(TAG, "admin product save error", t);
                view.showLoading(false);
                view.showError(context.getString(R.string.error_network));
            }
        });
    }

    public void loadProduct(Context context, long productId) {
        if (context == null || productId <= 0) {
            return;
        }
        ApiClient.getService().getProductDetail(productId)
                .enqueue(new Callback<BaseResponse<Product>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Product>> call,
                                           Response<BaseResponse<Product>> response) {
                        AdminProductEditContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "admin product detail failed http=" + response.code());
                            view.showError(context.getString(R.string.admin_product_load_failed));
                            return;
                        }
                        BaseResponse<Product> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(body.getMessage());
                            return;
                        }
                        view.showProduct(body.getData());
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Product>> call, Throwable t) {
                        AdminProductEditContract.View view = getView();
                        if (view != null) {
                            view.showError(context.getString(R.string.error_network));
                        }
                    }
                });
    }
}
