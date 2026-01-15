package com.wzh.suyuan.feature.address;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.Address;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressListPresenter extends BasePresenter<AddressListContract.View> {
    private static final String TAG = "AddressListPresenter";

    public void loadAddresses(Context context) {
        if (context == null) {
            return;
        }
        AddressListContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        ApiClient.getService().getAddresses().enqueue(new Callback<BaseResponse<List<Address>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Address>>> call,
                                   Response<BaseResponse<List<Address>>> response) {
                AddressListContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "address list failed http=" + response.code());
                    view.showError("地址加载失败");
                    return;
                }
                BaseResponse<List<Address>> body = response.body();
                if (!body.isSuccess()) {
                    view.showError(body.getMessage());
                    return;
                }
                List<Address> addresses = body.getData();
                if (addresses == null) {
                    addresses = new ArrayList<>();
                }
                if (addresses.isEmpty()) {
                    view.showEmpty("暂无收货地址");
                } else {
                    view.showAddresses(addresses);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Address>>> call, Throwable t) {
                AddressListContract.View view = getView();
                if (view == null) {
                    return;
                }
                Log.w(TAG, "address list error", t);
                view.showLoading(false);
                view.showError("网络异常，请稍后重试");
            }
        });
    }

    public void deleteAddress(Context context, Address address) {
        if (context == null || address == null || address.getId() == null) {
            return;
        }
        ApiClient.getService().deleteAddress(address.getId()).enqueue(new Callback<BaseResponse<Object>>() {
            @Override
            public void onResponse(Call<BaseResponse<Object>> call,
                                   Response<BaseResponse<Object>> response) {
                AddressListContract.View view = getView();
                if (view == null) {
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    view.showError("删除失败");
                    return;
                }
                BaseResponse<Object> body = response.body();
                if (!body.isSuccess()) {
                    view.showError(body.getMessage());
                    return;
                }
                loadAddresses(context);
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                AddressListContract.View view = getView();
                if (view != null) {
                    view.showError("网络异常，请稍后重试");
                }
            }
        });
    }

    public void setDefault(Context context, Address address) {
        if (context == null || address == null || address.getId() == null) {
            return;
        }
        ApiClient.getService().setDefaultAddress(address.getId()).enqueue(new Callback<BaseResponse<Address>>() {
            @Override
            public void onResponse(Call<BaseResponse<Address>> call,
                                   Response<BaseResponse<Address>> response) {
                AddressListContract.View view = getView();
                if (view == null) {
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    view.showError("设置默认地址失败");
                    return;
                }
                BaseResponse<Address> body = response.body();
                if (!body.isSuccess()) {
                    view.showError(body.getMessage());
                    return;
                }
                loadAddresses(context);
            }

            @Override
            public void onFailure(Call<BaseResponse<Address>> call, Throwable t) {
                AddressListContract.View view = getView();
                if (view != null) {
                    view.showError("网络异常，请稍后重试");
                }
            }
        });
    }
}
