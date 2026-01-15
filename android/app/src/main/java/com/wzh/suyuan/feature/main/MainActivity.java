package com.wzh.suyuan.feature.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.wzh.suyuan.R;
import com.wzh.suyuan.data.auth.AuthManager;
import com.wzh.suyuan.event.AuthEvent;
import com.wzh.suyuan.feature.auth.AuthActivity;
import com.wzh.suyuan.feature.main.fragment.ProfileFragment;
import com.wzh.suyuan.feature.cart.CartFragment;
import com.wzh.suyuan.feature.home.HomeFragment;
import com.wzh.suyuan.kit.NetworkUtils;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

public class MainActivity extends BaseActivity<MainContract.View, MainPresenter>
        implements MainContract.View {

    private RadioGroup navGroup;
    private HomeFragment homeFragment;
    private CartFragment cartFragment;
    private ProfileFragment profileFragment;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        navGroup = findViewById(R.id.main_nav_group);
        navGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.nav_home) {
                switchToFragment(getHomeFragment());
            } else if (checkedId == R.id.nav_cart) {
                switchToFragment(getCartFragment());
            } else if (checkedId == R.id.nav_profile) {
                switchToFragment(getProfileFragment());
            }
        });
    }

    @Override
    protected void initData() {
        if (!AuthManager.hasToken(this)) {
            navigateToAuth();
            return;
        }
        navGroup.check(R.id.nav_home);
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtils.showToast("当前无网络，请检查后重试");
            return;
        }
        if (presenter != null) {
            presenter.refreshProfile(this);
        }
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    public void onProfileUpdated() {
        if (profileFragment != null) {
            profileFragment.refreshUserInfo();
        }
    }

    @Override
    public void showError(String message) {
        ToastUtils.showToast(message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthEvent(AuthEvent event) {
        if (event != null && event.getType() == AuthEvent.Type.UNAUTHORIZED) {
            ToastUtils.showToast("登录已过期，请重新登录");
            navigateToAuth();
        }
    }

    private void switchToFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, fragment);
        transaction.commitAllowingStateLoss();
    }

    private HomeFragment getHomeFragment() {
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
        }
        return homeFragment;
    }

    private CartFragment getCartFragment() {
        if (cartFragment == null) {
            cartFragment = new CartFragment();
        }
        return cartFragment;
    }

    private ProfileFragment getProfileFragment() {
        if (profileFragment == null) {
            profileFragment = new ProfileFragment();
        }
        return profileFragment;
    }

    private void navigateToAuth() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}
