package com.wzh.suyuan.feature.admin.order;

import android.content.Context;
import android.util.Log;

import com.wzh.suyuan.R;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.AdminOrderShipRequest;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.OrderDetail;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderDetailPresenter extends BasePresenter<AdminOrderDetailContract.View> {
    private static final String TAG = "AdminOrderDetail";

    public void loadDetail(Context context, long orderId) {
        if (context == null || orderId <= 0) {
            return;
        }
        AdminOrderDetailContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        ApiClient.getService().getAdminOrderDetail(orderId)
                .enqueue(new Callback<BaseResponse<OrderDetail>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<OrderDetail>> call,
                                           Response<BaseResponse<OrderDetail>> response) {
                        AdminOrderDetailContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        view.showLoading(false);
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "admin order detail failed http=" + response.code());
                            view.showError(context.getString(R.string.admin_order_error));
                            return;
                        }
                        BaseResponse<OrderDetail> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(body.getMessage());
                            return;
                        }
                        view.showOrderDetail(body.getData());
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<OrderDetail>> call, Throwable t) {
                        AdminOrderDetailContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        Log.w(TAG, "admin order detail error", t);
                        view.showLoading(false);
                        view.showError(context.getString(R.string.error_network));
                    }
                });
    }

    public void shipOrder(Context context, long orderId, String company, String expressNo) {
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
                        AdminOrderDetailContract.View view = getView();
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
                        view.showOrderDetail(body.getData());
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<OrderDetail>> call, Throwable t) {
                        AdminOrderDetailContract.View view = getView();
                        if (view != null) {
                            view.showError(context.getString(R.string.error_network));
                        }
                    }
                });
    }
}
