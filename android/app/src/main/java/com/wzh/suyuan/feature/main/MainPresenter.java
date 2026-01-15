package com.wzh.suyuan.feature.main;

import android.content.Context;
import android.util.Log;

import com.wzh.suyuan.data.auth.AuthManager;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.AuthUser;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPresenter extends BasePresenter<MainContract.View> {
    private static final String TAG = "MainPresenter";

    public void refreshProfile(Context context) {
        ApiClient.getService().me().enqueue(new Callback<BaseResponse<AuthUser>>() {
            @Override
            public void onResponse(Call<BaseResponse<AuthUser>> call, Response<BaseResponse<AuthUser>> response) {
                MainContract.View view = getView();
                if (view == null) {
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "profile failed http=" + response.code());
                    view.showError("获取用户信息失败");
                    return;
                }
                BaseResponse<AuthUser> body = response.body();
                if (!body.isSuccess() || body.getData() == null) {
                    view.showError(body.getMessage());
                    return;
                }
                AuthManager.saveUser(context, body.getData());
                view.onProfileUpdated();
            }

            @Override
            public void onFailure(Call<BaseResponse<AuthUser>> call, Throwable t) {
                MainContract.View view = getView();
                if (view == null) {
                    return;
                }
                Log.w(TAG, "profile error", t);
                view.showError("网络异常，请稍后重试");
            }
        });
    }
}
