package com.wzh.suyuan.feature.trace;

import com.wzh.suyuan.network.model.TraceDetail;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface TraceDetailContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void showTraceDetail(TraceDetail detail);

        void showError(String message);
    }
}
