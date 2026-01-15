package com.wzh.suyuan.ui.activity.base;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.wzh.suyuan.ui.mvp.BaseView;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

public abstract class BaseActivity<V extends BaseView, P extends BasePresenter<V>>
        extends AppCompatActivity implements BaseView {

    protected P presenter;

    protected abstract int getLayoutResId();

    protected abstract void initView(Bundle savedInstanceState);

    protected abstract void initData();

    protected abstract P createPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutResId = getLayoutResId();
        if (layoutResId != 0) {
            setContentView(layoutResId);
        }
        presenter = createPresenter();
        if (presenter != null) {
            //noinspection unchecked
            presenter.attachView((V) this);
        }
        initView(savedInstanceState);
        initData();
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.detachView();
        }
        super.onDestroy();
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
