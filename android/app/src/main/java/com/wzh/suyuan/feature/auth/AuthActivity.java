package com.wzh.suyuan.feature.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.wzh.suyuan.R;
import com.wzh.suyuan.feature.main.MainActivity;
import com.wzh.suyuan.kit.NetworkUtils;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

public class AuthActivity extends BaseActivity<AuthContract.View, AuthPresenter>
        implements AuthContract.View {

    private RadioGroup modeGroup;
    private EditText inputUsername;
    private EditText inputPassword;
    private EditText inputPhone;
    private Button submitButton;
    private ProgressBar progressBar;
    private boolean isRegisterMode = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_auth;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        modeGroup = findViewById(R.id.auth_mode_group);
        inputUsername = findViewById(R.id.input_username);
        inputPassword = findViewById(R.id.input_password);
        inputPhone = findViewById(R.id.input_phone);
        submitButton = findViewById(R.id.button_submit);
        progressBar = findViewById(R.id.progress_loading);

        modeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.tab_register) {
                updateMode(true);
            } else {
                updateMode(false);
            }
        });
        submitButton.setOnClickListener(v -> submit());
        updateMode(false);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected AuthPresenter createPresenter() {
        return new AuthPresenter();
    }

    @Override
    public void showLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (submitButton != null) {
            submitButton.setEnabled(false);
        }
    }

    @Override
    public void hideLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (submitButton != null) {
            submitButton.setEnabled(true);
        }
    }

    @Override
    public void showError(String message) {
        ToastUtils.showToast(TextUtils.isEmpty(message) ? "请求失败" : message);
    }

    @Override
    public void onLoginSuccess() {
        ToastUtils.showToast("登录成功");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRegisterSuccess() {
        ToastUtils.showToast("注册成功，请登录");
        if (modeGroup != null) {
            modeGroup.check(R.id.tab_login);
        }
    }

    private void updateMode(boolean registerMode) {
        isRegisterMode = registerMode;
        if (inputPhone != null) {
            inputPhone.setVisibility(registerMode ? View.VISIBLE : View.GONE);
        }
        if (submitButton != null) {
            submitButton.setText(registerMode ? R.string.action_register : R.string.action_login);
        }
    }

    private void submit() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            ToastUtils.showToast("当前无网络，请检查后重试");
            return;
        }
        String username = inputUsername.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            ToastUtils.showToast("请输入账号和密码");
            return;
        }
        if (isRegisterMode) {
            presenter.register(this, username, password, phone);
        } else {
            presenter.login(this, username, password);
        }
    }
}
