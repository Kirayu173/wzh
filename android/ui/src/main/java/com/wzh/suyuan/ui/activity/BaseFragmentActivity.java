package com.wzh.suyuan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wzh.suyuan.ui.R;
import com.wzh.suyuan.ui.activity.base.BaseActivity;
import com.wzh.suyuan.ui.mvp.BaseView;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

public class BaseFragmentActivity extends BaseActivity<BaseView, BasePresenter<BaseView>> {

    private static final String EXTRA_FRAGMENT = "extra_fragment_class";

    public static void createFragment(Context context, Class<? extends Fragment> fragmentClass) {
        createFragment(context, fragmentClass, null);
    }

    public static void createFragment(Context context, Class<? extends Fragment> fragmentClass, Bundle args) {
        Intent intent = new Intent(context, BaseFragmentActivity.class);
        intent.putExtra(EXTRA_FRAGMENT, fragmentClass);
        if (args != null) {
            intent.putExtras(args);
        }
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_base;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    @Override
    protected void initData() {
        Class<?> fragmentClass = (Class<?>) getIntent().getSerializableExtra(EXTRA_FRAGMENT);
        if (fragmentClass == null) {
            return;
        }
        Fragment fragment = Fragment.instantiate(this, fragmentClass.getName(), getIntent().getExtras());
        addFragment(fragment);
    }

    @Override
    protected BasePresenter<BaseView> createPresenter() {
        return null;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            super.onBackPressed();
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        if (count > 1) {
            fm.popBackStackImmediate();
        } else {
            finish();
        }
    }

    public void addFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        String tag = fragment.getClass().getName();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment tempFragment = fm.findFragmentByTag(tag);
        if (tempFragment != null) {
            ft.replace(R.id.content_view, fragment, tag);
        } else {
            ft.add(R.id.content_view, fragment, tag);
        }
        ft.addToBackStack(tag);
        ft.commitAllowingStateLoss();
    }

    public Fragment getCurrentFragment() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            return null;
        }
        FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(count - 1);
        return getSupportFragmentManager().findFragmentByTag(entry.getName());
    }
}
