package com.wzh.suyuan.feature.admin.order;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.wzh.suyuan.R;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.AdminOrderShipRequest;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.OrderDetail;
import com.wzh.suyuan.network.model.OrderPage;
import com.wzh.suyuan.network.model.OrderSummary;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderListPresenter extends BasePresenter<AdminOrderListContract.View> {
    private static final String TAG = "AdminOrderList";

    public void loadOrders(Context context, String status, int page, int size) {
        if (context == null) {
            return;
        }
        AdminOrderListContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        ApiClient.getService().getAdminOrders(status, null, page, size)
                .enqueue(new Callback<BaseResponse<OrderPage>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<OrderPage>> call,
                                           Response<BaseResponse<OrderPage>> response) {
                        AdminOrderListContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        view.showLoading(false);
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "admin order list failed http=" + response.code());
                            view.showError(context.getString(R.string.admin_order_error));
                            return;
                        }
                        BaseResponse<OrderPage> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(body.getMessage());
                            return;
                        }
                        OrderPage pageData = body.getData();
                        List<OrderSummary> items = pageData == null ? new ArrayList<>() : pageData.getItems();
                        if (items == null) {
                            items = new ArrayList<>();
                        }
                        long total = pageData == null ? 0 : pageData.getTotal();
                        if (items.isEmpty() && page == 1) {
                            view.showEmpty(context.getString(R.string.admin_order_empty));
                        } else {
                            view.showOrders(items, page, size, total);
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<OrderPage>> call, Throwable t) {
                        AdminOrderListContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        Log.w(TAG, "admin order list error", t);
                        view.showLoading(false);
                        view.showError(context.getString(R.string.error_network));
                    }
                });
    }

    public void shipOrder(Context context, long orderId, String company, String expressNo,
                          String reloadStatus, int page, int size) {
        if (context == null || orderId <= 0) {
            return;
        }
        AdminOrderShipRequest request = new AdminOrderShipRequest();
        request.setExpressCompany(company);
        request.setExpressNo(expressNo);
        ApiClient.getService().shipAdminOrder(orderId, request)
                .enqueue(new Callback<BaseResponse<OrderDetail>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<OrderDetail>> call,
                                           Response<BaseResponse<OrderDetail>> response) {
                        AdminOrderListContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            view.showError(context.getString(R.string.admin_order_ship_failed));
                            return;
                        }
                        BaseResponse<OrderDetail> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(body.getMessage());
                            return;
                        }
                        loadOrders(context, reloadStatus, page, size);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<OrderDetail>> call, Throwable t) {
                        AdminOrderListContract.View view = getView();
                        if (view != null) {
                            view.showError(context.getString(R.string.error_network));
                        }
                    }
                });
    }
}
