package com.wzh.suyuan.feature.trace;

import java.util.List;

import com.wzh.suyuan.data.db.entity.ScanRecordEntity;
import com.wzh.suyuan.ui.mvp.BaseView;

public interface TraceRecordContract {
    interface View extends BaseView {
        void showLoading(boolean loading);

        void showRecords(List<ScanRecordEntity> records);

        void showEmpty(String message);

        void showError(String message);
    }
}
