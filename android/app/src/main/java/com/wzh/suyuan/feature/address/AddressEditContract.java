package com.wzh.suyuan.feature.address;

import com.wzh.suyuan.ui.mvp.BaseView;

public interface AddressEditContract {
    interface View extends BaseView {
        void showSaving(boolean saving);

        void onSaveSuccess();
    }
}
