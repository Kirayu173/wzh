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

public class OrderPayPresenter extends BasePresenter<OrderPayContract.View> {
    private static final String TAG = "OrderPayPresenter";

    public void payOrder(Context context, long orderId) {
        if (context == null || orderId <= 0) {
            return;
        }
        OrderPayContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        ApiClient.getService().payOrder(orderId).enqueue(new Callback<BaseResponse<OrderDetail>>() {
            @Override
            public void onResponse(Call<BaseResponse<OrderDetail>> call,
                                   Response<BaseResponse<OrderDetail>> response) {
                OrderPayContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "pay failed http=" + response.code());
                    view.showError("支付失败");
                    return;
                }
                BaseResponse<OrderDetail> body = response.body();
                if (!body.isSuccess()) {
                    view.showError(body.getMessage());
                    return;
                }
                view.onPaySuccess(body.getData());
            }

            @Override
            public void onFailure(Call<BaseResponse<OrderDetail>> call, Throwable t) {
                OrderPayContract.View view = getView();
                if (view == null) {
                    return;
                }
                Log.w(TAG, "pay error", t);
                view.showLoading(false);
                view.showError("网络异常，请稍后重试");
            }
        });
    }
}
