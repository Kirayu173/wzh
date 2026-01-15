package com.wzh.suyuan.feature.address;

import android.content.Context;
import android.util.Log;

import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.Address;
import com.wzh.suyuan.network.model.AddressRequest;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressEditPresenter extends BasePresenter<AddressEditContract.View> {
    private static final String TAG = "AddressEditPresenter";

    public void saveAddress(Context context, Long addressId, AddressRequest request) {
        if (context == null || request == null) {
            return;
        }
        AddressEditContract.View view = getView();
        if (view != null) {
            view.showSaving(true);
        }
        Callback<BaseResponse<Address>> callback = new Callback<BaseResponse<Address>>() {
            @Override
            public void onResponse(Call<BaseResponse<Address>> call,
                                   Response<BaseResponse<Address>> response) {
                AddressEditContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.showSaving(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "address save failed http=" + response.code());
                    view.showError("保存失败");
                    return;
                }
                BaseResponse<Address> body = response.body();
                if (!body.isSuccess()) {
                    view.showError(body.getMessage());
                    return;
                }
                view.onSaveSuccess();
            }

            @Override
            public void onFailure(Call<BaseResponse<Address>> call, Throwable t) {
                AddressEditContract.View view = getView();
                if (view != null) {
                    Log.w(TAG, "address save error", t);
                    view.showSaving(false);
                    view.showError("网络异常，请稍后重试");
                }
            }
        };
        if (addressId == null || addressId <= 0) {
            ApiClient.getService().createAddress(request).enqueue(callback);
        } else {
            ApiClient.getService().updateAddress(addressId, request).enqueue(callback);
        }
    }
}
