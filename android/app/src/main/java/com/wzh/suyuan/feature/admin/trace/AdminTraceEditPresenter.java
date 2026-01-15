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
        Call<BaseResponse<?>> call;
        if (batchId > 0) {
            call = ApiClient.getService().updateAdminTraceBatch(batchId, request);
        } else {
            call = ApiClient.getService().createAdminTraceBatch(request);
        }
        call.enqueue(new Callback<BaseResponse<?>>() {
            @Override
            public void onResponse(Call<BaseResponse<?>> call, Response<BaseResponse<?>> response) {
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
                BaseResponse<?> body = response.body();
                if (!body.isSuccess()) {
                    view.showError(body.getMessage());
                    return;
                }
                view.onSaveSuccess();
            }

            @Override
            public void onFailure(Call<BaseResponse<?>> call, Throwable t) {
                AdminTraceEditContract.View view = getView();
                if (view == null) {
                    return;
                }
                Log.w(TAG, "admin trace save error", t);
                view.showLoading(false);
                view.showError(context.getString(R.string.error_network));
            }
        });
    }
}
