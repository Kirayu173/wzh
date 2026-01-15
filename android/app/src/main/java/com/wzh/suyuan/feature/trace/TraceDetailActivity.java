package com.wzh.suyuan.feature.trace;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.TraceBatch;
import com.wzh.suyuan.network.model.TraceDetail;
import com.wzh.suyuan.network.model.TraceLogisticsNode;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

public class TraceDetailActivity extends BaseActivity<TraceDetailContract.View, TraceDetailPresenter>
        implements TraceDetailContract.View {

    public static final String EXTRA_TRACE_CODE = "extra_trace_code";

    private TextView traceCodeView;
    private TextView productNameView;
    private TextView originView;
    private TextView producerView;
    private TextView batchNoView;
    private TextView harvestDateView;
    private TextView processInfoView;
    private TextView testOrgView;
    private TextView testDateView;
    private TextView testResultView;
    private TextView reportUrlView;
    private TextView logisticsEmptyView;
    private RecyclerView logisticsList;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;

    private TraceNodeAdapter adapter;
    private String traceCode;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_trace_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        traceCodeView = findViewById(R.id.trace_detail_code);
        productNameView = findViewById(R.id.trace_detail_product);
        originView = findViewById(R.id.trace_detail_origin);
        producerView = findViewById(R.id.trace_detail_producer);
        batchNoView = findViewById(R.id.trace_detail_batch);
        harvestDateView = findViewById(R.id.trace_detail_harvest);
        processInfoView = findViewById(R.id.trace_detail_process);
        testOrgView = findViewById(R.id.trace_detail_test_org);
        testDateView = findViewById(R.id.trace_detail_test_date);
        testResultView = findViewById(R.id.trace_detail_test_result);
        reportUrlView = findViewById(R.id.trace_detail_report);
        logisticsEmptyView = findViewById(R.id.trace_detail_logistics_empty);
        logisticsList = findViewById(R.id.trace_detail_logistics);
        stateContainer = findViewById(R.id.trace_detail_state);
        stateText = findViewById(R.id.trace_detail_state_text);
        stateAction = findViewById(R.id.trace_detail_state_action);

        adapter = new TraceNodeAdapter();
        logisticsList.setLayoutManager(new LinearLayoutManager(this));
        logisticsList.setAdapter(adapter);
        stateAction.setOnClickListener(v -> loadDetail());
    }

    @Override
    protected void initData() {
        traceCode = getIntent().getStringExtra(EXTRA_TRACE_CODE);
        loadDetail();
    }

    @Override
    protected TraceDetailPresenter createPresenter() {
        return new TraceDetailPresenter();
    }

    @Override
    public void showLoading(boolean loading) {
        if (loading) {
            stateContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void showTraceDetail(TraceDetail detail) {
        if (detail == null || detail.getBatch() == null) {
            showError("暂无溯源信息");
            return;
        }
        TraceBatch batch = detail.getBatch();
        bindText(traceCodeView, getString(R.string.trace_code_value, safeText(batch.getTraceCode())), true);
        bindOptional(productNameView, R.string.trace_product_value, batch.getProductName());
        bindOptional(originView, R.string.trace_origin_value, batch.getOrigin());
        bindOptional(producerView, R.string.trace_producer_value, batch.getProducer());
        bindOptional(batchNoView, R.string.trace_batch_value, batch.getBatchNo());
        bindOptional(harvestDateView, R.string.trace_harvest_value, formatTime(batch.getHarvestDate()));
        bindOptional(processInfoView, R.string.trace_process_value, batch.getProcessInfo());
        bindOptional(testOrgView, R.string.trace_test_org_value, batch.getTestOrg());
        bindOptional(testDateView, R.string.trace_test_date_value, formatTime(batch.getTestDate()));
        bindOptional(testResultView, R.string.trace_test_result_value, batch.getTestResult());
        bindOptional(reportUrlView, R.string.trace_report_value, batch.getReportUrl());

        List<TraceLogisticsNode> nodes = detail.getLogistics();
        adapter.setItems(nodes);
        boolean hasNodes = nodes != null && !nodes.isEmpty();
        logisticsEmptyView.setVisibility(hasNodes ? View.GONE : View.VISIBLE);
        stateContainer.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        if (!TextUtils.isEmpty(message)) {
            ToastUtils.showToast(message);
        }
        showState(message == null ? getString(R.string.trace_detail_error) : message);
    }

    private void loadDetail() {
        if (TextUtils.isEmpty(traceCode)) {
            showState(getString(R.string.trace_code_invalid));
            return;
        }
        if (presenter != null) {
            presenter.loadTraceDetail(this, traceCode);
        }
    }

    private void showState(String message) {
        stateContainer.setVisibility(View.VISIBLE);
        if (message != null) {
            stateText.setText(message);
        }
    }

    private void bindText(TextView view, String text, boolean show) {
        if (view == null) {
            return;
        }
        view.setVisibility(show ? View.VISIBLE : View.GONE);
        view.setText(text);
    }

    private void bindOptional(TextView view, int labelResId, String value) {
        if (view == null) {
            return;
        }
        if (TextUtils.isEmpty(value)) {
            view.setVisibility(View.GONE);
            return;
        }
        view.setVisibility(View.VISIBLE);
        view.setText(getString(labelResId, value));
    }

    private String safeText(String value) {
        return TextUtils.isEmpty(value) ? "-" : value;
    }

    private String formatTime(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        return value.replace('T', ' ');
    }
}
