package com.wzh.suyuan.feature.admin.trace;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.TraceLogisticsRequest;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminTraceLogisticsActivity extends BaseActivity<AdminTraceLogisticsContract.View, AdminTraceLogisticsPresenter>
        implements AdminTraceLogisticsContract.View {

    public static final String EXTRA_TRACE_CODE = "extra_trace_code";

    private EditText timeInput;
    private EditText locationInput;
    private EditText statusInput;
    private Button saveButton;
    private String traceCode;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_admin_trace_logistics;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        timeInput = findViewById(R.id.admin_trace_logistics_time);
        locationInput = findViewById(R.id.admin_trace_logistics_location);
        statusInput = findViewById(R.id.admin_trace_logistics_status);
        saveButton = findViewById(R.id.admin_trace_logistics_save);

        traceCode = getIntent().getStringExtra(EXTRA_TRACE_CODE);
        if (TextUtils.isEmpty(timeInput.getText())) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            timeInput.setText(format.format(new Date()));
        }
        saveButton.setOnClickListener(v -> submit());
    }

    @Override
    protected void initData() {
    }

    @Override
    protected AdminTraceLogisticsPresenter createPresenter() {
        return new AdminTraceLogisticsPresenter();
    }

    @Override
    public void showLoading(boolean loading) {
        saveButton.setEnabled(!loading);
    }

    @Override
    public void onSaveSuccess() {
        ToastUtils.showToast(getString(R.string.admin_trace_logistics_success));
        finish();
    }

    @Override
    public void showError(String message) {
        if (!TextUtils.isEmpty(message)) {
            ToastUtils.showToast(message);
        }
    }

    private void submit() {
        String nodeTime = timeInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String status = statusInput.getText().toString().trim();
        if (TextUtils.isEmpty(nodeTime)) {
            ToastUtils.showToast(getString(R.string.admin_trace_logistics_time_required));
            return;
        }
        if (TextUtils.isEmpty(location)) {
            ToastUtils.showToast(getString(R.string.admin_trace_logistics_location_required));
            return;
        }
        if (TextUtils.isEmpty(status)) {
            ToastUtils.showToast(getString(R.string.admin_trace_logistics_status_required));
            return;
        }
        TraceLogisticsRequest request = new TraceLogisticsRequest();
        request.setNodeTime(nodeTime);
        request.setLocation(location);
        request.setStatusDesc(status);
        if (presenter != null) {
            presenter.addLogistics(this, traceCode, request);
        }
    }
}
