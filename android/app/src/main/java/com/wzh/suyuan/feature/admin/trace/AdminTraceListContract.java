package com.wzh.suyuan.feature.admin.trace;

import java.util.List;

import com.wzh.suyuan.network.model.TraceBatch;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface AdminTraceListContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void showBatches(List<TraceBatch> batches);

        void showEmpty(String message);

        void showError(String message);
    }
}
