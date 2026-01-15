package com.wzh.suyuan.feature.admin.product;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.wzh.suyuan.R;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.AdminProductStatusRequest;
import com.wzh.suyuan.network.model.AdminProductStockRequest;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.Product;
import com.wzh.suyuan.network.model.ProductPage;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductListPresenter extends BasePresenter<AdminProductListContract.View> {
    private static final String TAG = "AdminProductPresenter";

    public void loadProducts(Context context) {
        if (context == null) {
            return;
        }
        AdminProductListContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        ApiClient.getService().getAdminProducts(1, 50, null, null)
                .enqueue(new Callback<BaseResponse<ProductPage>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<ProductPage>> call,
                                           Response<BaseResponse<ProductPage>> response) {
                        AdminProductListContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        view.showLoading(false);
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "admin product list failed http=" + response.code());
                            view.showError(context.getString(R.string.admin_product_load_failed));
                            return;
                        }
                        BaseResponse<ProductPage> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(body.getMessage());
                            return;
                        }
                        ProductPage page = body.getData();
                        List<Product> items = page == null ? new ArrayList<>() : page.getItems();
                        if (items == null) {
                            items = new ArrayList<>();
                        }
                        if (items.isEmpty()) {
                            view.showEmpty(context.getString(R.string.admin_product_empty));
                        } else {
                            view.showProducts(items);
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<ProductPage>> call, Throwable t) {
                        AdminProductListContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        Log.w(TAG, "admin product list error", t);
                        view.showLoading(false);
                        view.showError(context.getString(R.string.error_network));
                    }
                });
    }

    public void updateStatus(Context context, Product product, String status) {
        if (context == null || product == null || product.getId() == null) {
            return;
        }
        AdminProductStatusRequest request = new AdminProductStatusRequest();
        request.setStatus(status);
        ApiClient.getService().updateAdminProductStatus(product.getId(), request)
                .enqueue(new Callback<BaseResponse<Object>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Object>> call,
                                           Response<BaseResponse<Object>> response) {
                        AdminProductListContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            view.showError(context.getString(R.string.admin_product_status_failed));
                            return;
                        }
                        BaseResponse<Object> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(body.getMessage());
                            return;
                        }
                        loadProducts(context);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                        AdminProductListContract.View view = getView();
                        if (view != null) {
                            view.showError(context.getString(R.string.error_network));
                        }
                    }
                });
    }

    public void updateStock(Context context, Product product, int stock) {
        if (context == null || product == null || product.getId() == null) {
            return;
        }
        AdminProductStockRequest request = new AdminProductStockRequest();
        request.setStock(stock);
        ApiClient.getService().updateAdminProductStock(product.getId(), request)
                .enqueue(new Callback<BaseResponse<Object>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Object>> call,
                                           Response<BaseResponse<Object>> response) {
                        AdminProductListContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            view.showError(context.getString(R.string.admin_product_stock_failed));
                            return;
                        }
                        BaseResponse<Object> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(body.getMessage());
                            return;
                        }
                        loadProducts(context);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                        AdminProductListContract.View view = getView();
                        if (view != null) {
                            view.showError(context.getString(R.string.error_network));
                        }
                    }
                });
    }

    public void deleteProduct(Context context, Product product) {
        if (context == null || product == null || product.getId() == null) {
            return;
        }
        ApiClient.getService().deleteAdminProduct(product.getId())
                .enqueue(new Callback<BaseResponse<Object>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Object>> call,
                                           Response<BaseResponse<Object>> response) {
                        AdminProductListContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            view.showError(context.getString(R.string.admin_product_delete_failed));
                            return;
                        }
                        BaseResponse<Object> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(body.getMessage());
                            return;
                        }
                        loadProducts(context);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                        AdminProductListContract.View view = getView();
                        if (view != null) {
                            view.showError(context.getString(R.string.error_network));
                        }
                    }
                });
    }
}
