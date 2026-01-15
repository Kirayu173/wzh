package com.wzh.suyuan.feature.admin.trace;

import android.graphics.Bitmap;

import com.wzh.suyuan.ui.mvp.BaseView;

public interface AdminTraceQrCodeContract {
    interface View extends BaseView {
        void showQrCode(Bitmap bitmap);

        void showError(String message);
    }
}
