package com.wzh.suyuan.feature.admin.trace;

import android.content.Context;
import android.util.Log;

import com.wzh.suyuan.R;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.TraceLogisticsCreateResponse;
import com.wzh.suyuan.network.model.TraceLogisticsRequest;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTraceLogisticsPresenter extends BasePresenter<AdminTraceLogisticsContract.View> {
    private static final String TAG = "AdminTraceLogistics";

    public void addLogistics(Context context, String traceCode, TraceLogisticsRequest request) {
        if (context == null || traceCode == null || traceCode.isEmpty() || request == null) {
            return;
        }
        AdminTraceLogisticsContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        ApiClient.getService().createAdminLogistics(traceCode, request)
                .enqueue(new Callback<BaseResponse<TraceLogisticsCreateResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<TraceLogisticsCreateResponse>> call,
                                           Response<BaseResponse<TraceLogisticsCreateResponse>> response) {
                        AdminTraceLogisticsContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        view.showLoading(false);
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "admin logistics create failed http=" + response.code());
                            view.showError(context.getString(R.string.admin_trace_logistics_failed));
                            return;
                        }
                        BaseResponse<TraceLogisticsCreateResponse> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(body.getMessage());
                            return;
                        }
                        view.onSaveSuccess();
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<TraceLogisticsCreateResponse>> call, Throwable t) {
                        AdminTraceLogisticsContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        Log.w(TAG, "admin logistics create error", t);
                        view.showLoading(false);
                        view.showError(context.getString(R.string.error_network));
                    }
                });
    }
}
