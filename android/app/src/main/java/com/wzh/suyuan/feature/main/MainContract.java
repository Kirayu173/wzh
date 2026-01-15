package com.wzh.suyuan.feature.main;

import com.wzh.suyuan.ui.mvp.BaseView;

public interface MainContract {
    interface View extends BaseView {
        void onProfileUpdated();
    }
}
