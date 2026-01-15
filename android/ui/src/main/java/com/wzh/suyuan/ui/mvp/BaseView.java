package com.wzh.suyuan.ui.mvp;

public interface BaseView {
    void showLoading();

    void hideLoading();

    void showError(String message);
}
