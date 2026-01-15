package com.wzh.suyuan.feature.admin.trace;

import com.wzh.suyuan.ui.mvp.BaseView;

public interface AdminTraceLogisticsContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void onSaveSuccess();

        void showError(String message);
    }
}
