package com.wzh.suyuan.feature.order;

import android.content.Context;
import android.util.Log;

import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.OrderDetail;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailPresenter extends BasePresenter<OrderDetailContract.View> {
    private static final String TAG = "OrderDetailPresenter";

    public void loadOrderDetail(Context context, long orderId) {
        if (context == null || orderId <= 0) {
            return;
        }
        OrderDetailContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        ApiClient.getService().getOrderDetail(orderId).enqueue(new Callback<BaseResponse<OrderDetail>>() {
            @Override
            public void onResponse(Call<BaseResponse<OrderDetail>> call,
                                   Response<BaseResponse<OrderDetail>> response) {
                OrderDetailContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "order detail failed http=" + response.code());
                    view.showError("订单加载失败");
                    return;
                }
                BaseResponse<OrderDetail> body = response.body();
                if (!body.isSuccess() || body.getData() == null) {
                    view.showError(body.getMessage());
                    return;
                }
                view.showOrder(body.getData());
            }

            @Override
            public void onFailure(Call<BaseResponse<OrderDetail>> call, Throwable t) {
                OrderDetailContract.View view = getView();
                if (view == null) {
                    return;
                }
                Log.w(TAG, "order detail error", t);
                view.showLoading(false);
                view.showError("网络异常，请稍后重试");
            }
        });
    }

    public void cancelOrder(Context context, long orderId) {
        if (context == null || orderId <= 0) {
            return;
        }
        OrderDetailContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        ApiClient.getService().cancelOrder(orderId).enqueue(new Callback<BaseResponse<OrderDetail>>() {
            @Override
            public void onResponse(Call<BaseResponse<OrderDetail>> call,
                                   Response<BaseResponse<OrderDetail>> response) {
                OrderDetailContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    view.showError("取消订单失败");
                    return;
                }
                BaseResponse<OrderDetail> body = response.body();
                if (!body.isSuccess() || body.getData() == null) {
                    view.showError(body.getMessage());
                    return;
                }
                view.showOrder(body.getData());
            }

            @Override
            public void onFailure(Call<BaseResponse<OrderDetail>> call, Throwable t) {
                OrderDetailContract.View view = getView();
                if (view != null) {
                    view.showLoading(false);
                    view.showError("网络异常，请稍后重试");
                }
            }
        });
    }

    public void confirmOrder(Context context, long orderId) {
        if (context == null || orderId <= 0) {
            return;
        }
        OrderDetailContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        ApiClient.getService().confirmOrder(orderId).enqueue(new Callback<BaseResponse<OrderDetail>>() {
            @Override
            public void onResponse(Call<BaseResponse<OrderDetail>> call,
                                   Response<BaseResponse<OrderDetail>> response) {
                OrderDetailContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    view.showError("确认收货失败");
                    return;
                }
                BaseResponse<OrderDetail> body = response.body();
                if (!body.isSuccess() || body.getData() == null) {
                    view.showError(body.getMessage());
                    return;
                }
                view.showOrder(body.getData());
            }

            @Override
            public void onFailure(Call<BaseResponse<OrderDetail>> call, Throwable t) {
                OrderDetailContract.View view = getView();
                if (view != null) {
                    view.showLoading(false);
                    view.showError("网络异常，请稍后重试");
                }
            }
        });
    }
}
