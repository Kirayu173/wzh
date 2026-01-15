package com.wzh.suyuan.feature.admin.trace;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.TraceBatchRequest;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

public class AdminTraceEditActivity extends BaseActivity<AdminTraceEditContract.View, AdminTraceEditPresenter>
        implements AdminTraceEditContract.View {

    public static final String EXTRA_BATCH_ID = "extra_batch_id";
    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    public static final String EXTRA_BATCH_NO = "extra_batch_no";
    public static final String EXTRA_ORIGIN = "extra_origin";
    public static final String EXTRA_PRODUCER = "extra_producer";
    public static final String EXTRA_HARVEST_DATE = "extra_harvest_date";
    public static final String EXTRA_PROCESS_INFO = "extra_process_info";
    public static final String EXTRA_TEST_ORG = "extra_test_org";
    public static final String EXTRA_TEST_DATE = "extra_test_date";
    public static final String EXTRA_TEST_RESULT = "extra_test_result";
    public static final String EXTRA_REPORT_URL = "extra_report_url";

    private EditText productIdInput;
    private EditText batchNoInput;
    private EditText originInput;
    private EditText producerInput;
    private EditText harvestDateInput;
    private EditText processInput;
    private EditText testOrgInput;
    private EditText testDateInput;
    private EditText testResultInput;
    private EditText reportInput;
    private Button saveButton;

    private long batchId;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_admin_trace_edit;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        productIdInput = findViewById(R.id.admin_trace_input_product_id);
        batchNoInput = findViewById(R.id.admin_trace_input_batch_no);
        originInput = findViewById(R.id.admin_trace_input_origin);
        producerInput = findViewById(R.id.admin_trace_input_producer);
        harvestDateInput = findViewById(R.id.admin_trace_input_harvest_date);
        processInput = findViewById(R.id.admin_trace_input_process);
        testOrgInput = findViewById(R.id.admin_trace_input_test_org);
        testDateInput = findViewById(R.id.admin_trace_input_test_date);
        testResultInput = findViewById(R.id.admin_trace_input_test_result);
        reportInput = findViewById(R.id.admin_trace_input_report);
        saveButton = findViewById(R.id.admin_trace_save);

        if (getIntent() != null) {
            batchId = getIntent().getLongExtra(EXTRA_BATCH_ID, 0L);
            long productId = getIntent().getLongExtra(EXTRA_PRODUCT_ID, 0L);
            productIdInput.setText(productId == 0L ? "" : String.valueOf(productId));
            batchNoInput.setText(getIntent().getStringExtra(EXTRA_BATCH_NO));
            originInput.setText(getIntent().getStringExtra(EXTRA_ORIGIN));
            producerInput.setText(getIntent().getStringExtra(EXTRA_PRODUCER));
            harvestDateInput.setText(getIntent().getStringExtra(EXTRA_HARVEST_DATE));
            processInput.setText(getIntent().getStringExtra(EXTRA_PROCESS_INFO));
            testOrgInput.setText(getIntent().getStringExtra(EXTRA_TEST_ORG));
            testDateInput.setText(getIntent().getStringExtra(EXTRA_TEST_DATE));
            testResultInput.setText(getIntent().getStringExtra(EXTRA_TEST_RESULT));
            reportInput.setText(getIntent().getStringExtra(EXTRA_REPORT_URL));
        }

        saveButton.setOnClickListener(v -> submit());
    }

    @Override
    protected void initData() {
    }

    @Override
    protected AdminTraceEditPresenter createPresenter() {
        return new AdminTraceEditPresenter();
    }

    @Override
    public void showLoading(boolean loading) {
        saveButton.setEnabled(!loading);
    }

    @Override
    public void onSaveSuccess() {
        ToastUtils.showToast(getString(R.string.admin_trace_save_success));
        finish();
    }

    @Override
    public void showError(String message) {
        if (!TextUtils.isEmpty(message)) {
            ToastUtils.showToast(message);
        }
    }

    private void submit() {
        String productIdText = productIdInput.getText().toString().trim();
        String origin = originInput.getText().toString().trim();
        String producer = producerInput.getText().toString().trim();
        if (productIdText.isEmpty()) {
            ToastUtils.showToast(getString(R.string.admin_trace_product_required));
            return;
        }
        long productId;
        try {
            productId = Long.parseLong(productIdText);
        } catch (NumberFormatException ex) {
            ToastUtils.showToast(getString(R.string.admin_trace_product_format_error));
            return;
        }
        if (productId <= 0) {
            ToastUtils.showToast(getString(R.string.admin_trace_product_format_error));
            return;
        }
        if (origin.isEmpty()) {
            ToastUtils.showToast(getString(R.string.admin_trace_origin_required));
            return;
        }
        if (producer.isEmpty()) {
            ToastUtils.showToast(getString(R.string.admin_trace_producer_required));
            return;
        }
        TraceBatchRequest request = new TraceBatchRequest();
        request.setProductId(productId);
        request.setBatchNo(batchNoInput.getText().toString().trim());
        request.setOrigin(origin);
        request.setProducer(producer);
        request.setHarvestDate(harvestDateInput.getText().toString().trim());
        request.setProcessInfo(processInput.getText().toString().trim());
        request.setTestOrg(testOrgInput.getText().toString().trim());
        request.setTestDate(testDateInput.getText().toString().trim());
        request.setTestResult(testResultInput.getText().toString().trim());
        request.setReportUrl(reportInput.getText().toString().trim());
        if (presenter != null) {
            presenter.saveBatch(this, batchId, request);
        }
    }
}
