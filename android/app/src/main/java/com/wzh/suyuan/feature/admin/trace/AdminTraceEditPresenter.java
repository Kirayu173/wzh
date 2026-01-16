package com.wzh.suyuan.feature.admin.trace;

import android.content.Context;
import android.util.Log;

import com.wzh.suyuan.R;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.TraceBatchRequest;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTraceEditPresenter extends BasePresenter<AdminTraceEditContract.View> {
    private static final String TAG = "AdminTraceEdit";

    public void saveBatch(Context context, long batchId, TraceBatchRequest request) {
        if (context == null || request == null) {
            return;
        }
        AdminTraceEditContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        if (batchId > 0) {
            ApiClient.getService()
                    .updateAdminTraceBatch(batchId, request)
                    .enqueue(createSaveCallback(context));
        } else {
            ApiClient.getService()
                    .createAdminTraceBatch(request)
                    .enqueue(createSaveCallback(context));
        }
    }

    private <T> Callback<BaseResponse<T>> createSaveCallback(Context context) {
        return new Callback<BaseResponse<T>>() {
            @Override
            public void onResponse(Call<BaseResponse<T>> call, Response<BaseResponse<T>> response) {
                AdminTraceEditContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "admin trace save failed http=" + response.code());
                    view.showError(context.getString(R.string.admin_trace_save_failed));
                    return;
                }
                BaseResponse<T> body = response.body();
                if (!body.isSuccess()) {
                    view.showError(body.getMessage());
                    return;
                }
                view.onSaveSuccess();
            }

            @Override
            public void onFailure(Call<BaseResponse<T>> call, Throwable t) {
                AdminTraceEditContract.View view = getView();
                if (view == null) {
                    return;
                }
                Log.w(TAG, "admin trace save error", t);
                view.showLoading(false);
                view.showError(context.getString(R.string.error_network));
            }
        };
    }
}
