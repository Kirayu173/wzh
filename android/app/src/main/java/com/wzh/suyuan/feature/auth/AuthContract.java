package com.wzh.suyuan.feature.auth;

import com.wzh.suyuan.ui.mvp.BaseView;

public interface AuthContract {
    interface View extends BaseView {
        void onLoginSuccess();

        void onRegisterSuccess();
    }
}
