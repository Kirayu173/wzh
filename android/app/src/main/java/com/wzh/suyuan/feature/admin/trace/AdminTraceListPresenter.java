package com.wzh.suyuan.feature.admin.trace;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.wzh.suyuan.R;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.TraceBatch;
import com.wzh.suyuan.network.model.TraceBatchPage;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTraceListPresenter extends BasePresenter<AdminTraceListContract.View> {
    private static final String TAG = "AdminTraceList";

    public void loadBatches(Context context, int page, int size) {
        if (context == null) {
            return;
        }
        AdminTraceListContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        ApiClient.getService().getAdminTraceBatches(page, size, null)
                .enqueue(new Callback<BaseResponse<TraceBatchPage>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<TraceBatchPage>> call,
                                           Response<BaseResponse<TraceBatchPage>> response) {
                        AdminTraceListContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        view.showLoading(false);
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "admin trace list failed http=" + response.code());
                            view.showError(context.getString(R.string.admin_trace_error));
                            return;
                        }
                        BaseResponse<TraceBatchPage> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(body.getMessage());
                            return;
                        }
                        TraceBatchPage pageData = body.getData();
                        List<TraceBatch> items = pageData == null ? new ArrayList<>() : pageData.getItems();
                        if (items == null) {
                            items = new ArrayList<>();
                        }
                        long total = pageData == null ? 0 : pageData.getTotal();
                        if (items.isEmpty() && page == 1) {
                            view.showEmpty(context.getString(R.string.admin_trace_empty));
                        } else {
                            view.showBatches(items, page, size, total);
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<TraceBatchPage>> call, Throwable t) {
                        AdminTraceListContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        Log.w(TAG, "admin trace list error", t);
                        view.showLoading(false);
                        view.showError(context.getString(R.string.error_network));
                    }
                });
    }

    public void deleteBatch(Context context, TraceBatch batch, int page, int size) {
        if (context == null || batch == null || batch.getId() == null) {
            return;
        }
        ApiClient.getService().deleteAdminTraceBatch(batch.getId())
                .enqueue(new Callback<BaseResponse<Object>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Object>> call,
                                           Response<BaseResponse<Object>> response) {
                        AdminTraceListContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            view.showError(context.getString(R.string.admin_trace_delete_failed));
                            return;
                        }
                        BaseResponse<Object> body = response.body();
                        if (!body.isSuccess()) {
                            view.showError(body.getMessage());
                            return;
                        }
                        loadBatches(context, page, size);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                        AdminTraceListContract.View view = getView();
                        if (view != null) {
                            view.showError(context.getString(R.string.error_network));
                        }
                    }
                });
    }
}
