package com.wzh.suyuan.ui.mvp;

public interface IPresenter<V extends BaseView> {
    void attachView(V view);

    void detachView();

    boolean isAttached();
}
