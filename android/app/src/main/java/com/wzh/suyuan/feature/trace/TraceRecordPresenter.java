package com.wzh.suyuan.feature.trace;

import android.content.Context;

import java.util.List;

import com.wzh.suyuan.AppExecutors;
import com.wzh.suyuan.data.db.AppDatabase;
import com.wzh.suyuan.data.db.entity.ScanRecordEntity;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

public class TraceRecordPresenter extends BasePresenter<TraceRecordContract.View> {
    public void loadRecords(Context context) {
        TraceRecordContract.View view = getView();
        if (view == null) {
            return;
        }
        view.showLoading(true);
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                List<ScanRecordEntity> records = AppDatabase.getInstance(context)
                        .scanRecordDao()
                        .getAll();
                AppExecutors.getInstance().mainThread().execute(() -> {
                    TraceRecordContract.View target = getView();
                    if (target == null) {
                        return;
                    }
                    target.showLoading(false);
                    if (records == null || records.isEmpty()) {
                        target.showEmpty("暂无扫码记录");
                    } else {
                        target.showRecords(records);
                    }
                });
            } catch (Exception ex) {
                AppExecutors.getInstance().mainThread().execute(() -> {
                    TraceRecordContract.View target = getView();
                    if (target == null) {
                        return;
                    }
                    target.showLoading(false);
                    target.showError("扫码记录加载失败");
                });
            }
        });
    }
}
