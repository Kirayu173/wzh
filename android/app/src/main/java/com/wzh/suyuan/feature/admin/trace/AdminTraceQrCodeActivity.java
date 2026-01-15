package com.wzh.suyuan.feature.admin.trace;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

public class AdminTraceQrCodeActivity extends BaseActivity<AdminTraceQrCodeContract.View, AdminTraceQrCodePresenter>
        implements AdminTraceQrCodeContract.View {

    public static final String EXTRA_TRACE_CODE = "extra_trace_code";

    private ImageView imageView;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;
    private String traceCode;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_admin_trace_qrcode;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        imageView = findViewById(R.id.admin_trace_qrcode_image);
        stateContainer = findViewById(R.id.admin_trace_qrcode_state);
        stateText = findViewById(R.id.admin_trace_qrcode_state_text);
        stateAction = findViewById(R.id.admin_trace_qrcode_state_action);
        traceCode = getIntent().getStringExtra(EXTRA_TRACE_CODE);
        stateAction.setOnClickListener(v -> loadQrCode());
    }

    @Override
    protected void initData() {
        loadQrCode();
    }

    @Override
    protected AdminTraceQrCodePresenter createPresenter() {
        return new AdminTraceQrCodePresenter();
    }

    @Override
    public void showQrCode(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        stateContainer.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        if (!TextUtils.isEmpty(message)) {
            ToastUtils.showToast(message);
            stateText.setText(message);
        }
        stateContainer.setVisibility(View.VISIBLE);
    }

    private void loadQrCode() {
        if (presenter != null) {
            presenter.loadQrCode(traceCode, this);
        }
    }
}
