package com.wzh.suyuan.feature.auth;

import android.content.Context;
import android.util.Log;

import com.wzh.suyuan.data.auth.AuthManager;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.LoginRequest;
import com.wzh.suyuan.network.model.LoginResponse;
import com.wzh.suyuan.network.model.RegisterRequest;
import com.wzh.suyuan.network.model.RegisterResponse;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthPresenter extends BasePresenter<AuthContract.View> {
    private static final String TAG = "AuthPresenter";

    public void login(Context context, String username, String password) {
        AuthContract.View view = getView();
        if (view == null) {
            return;
        }
        view.showLoading();
        ApiClient.getService().login(new LoginRequest(username, password))
                .enqueue(new Callback<BaseResponse<LoginResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<LoginResponse>> call,
                                           Response<BaseResponse<LoginResponse>> response) {
                        AuthContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        view.hideLoading();
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "login failed http=" + response.code());
                            view.showError("登录失败，请稍后重试");
                            return;
                        }
                        BaseResponse<LoginResponse> body = response.body();
                        if (!body.isSuccess() || body.getData() == null) {
                            view.showError(fallbackMessage(body.getMessage(), "登录失败"));
                            return;
                        }
                        LoginResponse data = body.getData();
                        AuthManager.saveSession(context, data.getToken(), data.getExpireAt(), data.getUser());
                        Log.i(TAG, "login success");
                        view.onLoginSuccess();
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<LoginResponse>> call, Throwable t) {
                        AuthContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        view.hideLoading();
                        Log.w(TAG, "login error", t);
                        view.showError("网络异常，请稍后重试");
                    }
                });
    }

    public void register(Context context, String username, String password, String phone) {
        AuthContract.View view = getView();
        if (view == null) {
            return;
        }
        view.showLoading();
        ApiClient.getService().register(new RegisterRequest(username, password, phone))
                .enqueue(new Callback<BaseResponse<RegisterResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<RegisterResponse>> call,
                                           Response<BaseResponse<RegisterResponse>> response) {
                        AuthContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        view.hideLoading();
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "register failed http=" + response.code());
                            view.showError("注册失败，请稍后重试");
                            return;
                        }
                        BaseResponse<RegisterResponse> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(fallbackMessage(body.getMessage(), "注册失败"));
                            return;
                        }
                        Log.i(TAG, "register success");
                        view.onRegisterSuccess();
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<RegisterResponse>> call, Throwable t) {
                        AuthContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        view.hideLoading();
                        Log.w(TAG, "register error", t);
                        view.showError("网络异常，请稍后重试");
                    }
                });
    }

    private String fallbackMessage(String message, String fallback) {
        return (message == null || message.isEmpty()) ? fallback : message;
    }
}
