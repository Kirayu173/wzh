package com.wzh.suyuan.feature.trace;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.wzh.suyuan.AppExecutors;
import com.wzh.suyuan.data.db.AppDatabase;
import com.wzh.suyuan.data.db.entity.ScanRecordEntity;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.TraceBatch;
import com.wzh.suyuan.network.model.TraceDetail;
import com.wzh.suyuan.network.model.TraceLogisticsNode;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TraceDetailPresenter extends BasePresenter<TraceDetailContract.View> {
    private static final String TAG = "TraceDetailPresenter";

    public void loadTraceDetail(Context context, String traceCode) {
        TraceDetailContract.View view = getView();
        if (view == null) {
            return;
        }
        view.showLoading(true);
        ApiClient.getService().getTraceDetail(traceCode)
                .enqueue(new Callback<BaseResponse<TraceDetail>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<TraceDetail>> call,
                                           Response<BaseResponse<TraceDetail>> response) {
                        TraceDetailContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        view.showLoading(false);
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "trace detail failed http=" + response.code());
                            view.showError("溯源信息加载失败");
                            return;
                        }
                        BaseResponse<TraceDetail> body = response.body();
                        if (!body.isSuccess() || body.getData() == null) {
                            view.showError(body.getMessage());
                            return;
                        }
                        TraceDetail detail = body.getData();
                        sortLogistics(detail);
                        view.showTraceDetail(detail);
                        saveScanRecord(context, detail, traceCode);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<TraceDetail>> call, Throwable t) {
                        TraceDetailContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        view.showLoading(false);
                        Log.w(TAG, "trace detail error", t);
                        view.showError("网络异常，请稍后重试");
                    }
                });
    }

    private void sortLogistics(TraceDetail detail) {
        if (detail == null || detail.getLogistics() == null) {
            return;
        }
        List<TraceLogisticsNode> nodes = new ArrayList<>(detail.getLogistics());
        Collections.sort(nodes, new Comparator<TraceLogisticsNode>() {
            @Override
            public int compare(TraceLogisticsNode left, TraceLogisticsNode right) {
                return safeTime(right).compareTo(safeTime(left));
            }
        });
        detail.setLogistics(nodes);
    }

    private String safeTime(TraceLogisticsNode node) {
        if (node == null || node.getNodeTime() == null) {
            return "";
        }
        return node.getNodeTime();
    }

    private void saveScanRecord(Context context, TraceDetail detail, String traceCode) {
        if (context == null || detail == null) {
            return;
        }
        TraceBatch batch = detail.getBatch();
        String code = traceCode;
        if (batch != null && !TextUtils.isEmpty(batch.getTraceCode())) {
            code = batch.getTraceCode();
        }
        if (TextUtils.isEmpty(code)) {
            return;
        }
        String productName = batch == null ? null : batch.getProductName();
        long now = System.currentTimeMillis();
        final String finalCode = code;
        final String finalProductName = productName;
        final long finalNow = now;
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                ScanRecordEntity record = new ScanRecordEntity();
                record.setTraceCode(finalCode);
                record.setScanTime(finalNow);
                record.setProductName(finalProductName);
                AppDatabase.getInstance(context).scanRecordDao().insert(record);
            } catch (Exception ex) {
                Log.w(TAG, "save scan record failed", ex);
            }
        });
    }
}
