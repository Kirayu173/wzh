package com.wzh.suyuan.ui.mvp.base;

import java.lang.ref.WeakReference;

import com.wzh.suyuan.ui.mvp.BaseView;
import com.wzh.suyuan.ui.mvp.IPresenter;

public class BasePresenter<V extends BaseView> implements IPresenter<V> {
    private WeakReference<V> viewRef;

    @Override
    public void attachView(V view) {
        viewRef = new WeakReference<>(view);
    }

    @Override
    public void detachView() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }

    @Override
    public boolean isAttached() {
        return viewRef != null && viewRef.get() != null;
    }

    protected V getView() {
        return viewRef == null ? null : viewRef.get();
    }
}
