package com.wzh.suyuan.feature.trace;

import com.wzh.suyuan.ui.mvp.BaseView;

public interface TraceScanContract {
    interface View extends BaseView {
        void showScanError(String message);

        void openTraceDetail(String traceCode);
    }
}
