package com.wzh.suyuan.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wzh.suyuan.ui.mvp.BaseView;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment
        implements BaseView {

    protected P presenter;

    protected abstract int getLayoutResId();

    protected abstract void initView(View rootView, @Nullable Bundle savedInstanceState);

    protected abstract void initData();

    protected abstract P createPresenter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        int layoutResId = getLayoutResId();
        if (layoutResId == 0) {
            return null;
        }
        View rootView = inflater.inflate(layoutResId, container, false);
        presenter = createPresenter();
        if (presenter != null) {
            //noinspection unchecked
            presenter.attachView((V) this);
        }
        initView(rootView, savedInstanceState);
        initData();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        if (presenter != null) {
            presenter.detachView();
        }
        super.onDestroyView();
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showError(String message) {
    }
}
