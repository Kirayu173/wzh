package com.wzh.suyuan.feature.admin.trace;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.TraceBatch;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

import java.util.List;

public class AdminTraceListActivity extends BaseActivity<AdminTraceListContract.View, AdminTraceListPresenter>
        implements AdminTraceListContract.View {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;
    private Button addButton;
    private AdminTraceAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_admin_trace_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        refreshLayout = findViewById(R.id.admin_trace_refresh);
        recyclerView = findViewById(R.id.admin_trace_list);
        stateContainer = findViewById(R.id.admin_trace_state_container);
        stateText = findViewById(R.id.admin_trace_state_text);
        stateAction = findViewById(R.id.admin_trace_state_action);
        addButton = findViewById(R.id.admin_trace_add);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminTraceAdapter();
        adapter.setTraceActionListener(new AdminTraceAdapter.TraceActionListener() {
            @Override
            public void onEdit(TraceBatch batch) {
                openEdit(batch);
            }

            @Override
            public void onQrCode(TraceBatch batch) {
                if (batch == null) {
                    return;
                }
                Intent intent = new Intent(AdminTraceListActivity.this, AdminTraceQrCodeActivity.class);
                intent.putExtra(AdminTraceQrCodeActivity.EXTRA_TRACE_CODE, batch.getTraceCode());
                startActivity(intent);
            }

            @Override
            public void onLogistics(TraceBatch batch) {
                if (batch == null) {
                    return;
                }
                Intent intent = new Intent(AdminTraceListActivity.this, AdminTraceLogisticsActivity.class);
                intent.putExtra(AdminTraceLogisticsActivity.EXTRA_TRACE_CODE, batch.getTraceCode());
                startActivity(intent);
            }

            @Override
            public void onDelete(TraceBatch batch) {
                if (batch == null) {
                    return;
                }
                new AlertDialog.Builder(AdminTraceListActivity.this)
                        .setTitle(R.string.action_delete)
                        .setMessage(R.string.admin_trace_delete_confirm)
                        .setNegativeButton(R.string.action_cancel, null)
                        .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                            if (presenter != null) {
                                presenter.deleteBatch(AdminTraceListActivity.this, batch);
                            }
                        })
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> openEdit(null));
        refreshLayout.setOnRefreshListener(this::loadBatches);
        stateAction.setOnClickListener(v -> loadBatches());
    }

    @Override
    protected void initData() {
        loadBatches();
    }

    @Override
    protected AdminTraceListPresenter createPresenter() {
        return new AdminTraceListPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBatches();
    }

    @Override
    public void showLoading(boolean loading) {
        refreshLayout.setRefreshing(loading);
    }

    @Override
    public void showBatches(List<TraceBatch> batches) {
        adapter.setItems(batches);
        showState(false, null);
    }

    @Override
    public void showEmpty(String message) {
        adapter.setItems(null);
        showState(true, message);
    }

    @Override
    public void showError(String message) {
        if (!TextUtils.isEmpty(message)) {
            ToastUtils.showToast(message);
        }
        if (adapter != null && adapter.getItemCount() == 0) {
            showState(true, message);
        }
    }

    private void showState(boolean show, String message) {
        stateContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        if (message != null) {
            stateText.setText(message);
        }
    }

    private void loadBatches() {
        if (presenter != null) {
            presenter.loadBatches(this);
        }
    }

    private void openEdit(TraceBatch batch) {
        Intent intent = new Intent(this, AdminTraceEditActivity.class);
        if (batch != null) {
            intent.putExtra(AdminTraceEditActivity.EXTRA_BATCH_ID, batch.getId());
            intent.putExtra(AdminTraceEditActivity.EXTRA_PRODUCT_ID, batch.getProductId());
            intent.putExtra(AdminTraceEditActivity.EXTRA_BATCH_NO, batch.getBatchNo());
            intent.putExtra(AdminTraceEditActivity.EXTRA_ORIGIN, batch.getOrigin());
            intent.putExtra(AdminTraceEditActivity.EXTRA_PRODUCER, batch.getProducer());
            intent.putExtra(AdminTraceEditActivity.EXTRA_HARVEST_DATE, batch.getHarvestDate());
            intent.putExtra(AdminTraceEditActivity.EXTRA_PROCESS_INFO, batch.getProcessInfo());
            intent.putExtra(AdminTraceEditActivity.EXTRA_TEST_ORG, batch.getTestOrg());
            intent.putExtra(AdminTraceEditActivity.EXTRA_TEST_DATE, batch.getTestDate());
            intent.putExtra(AdminTraceEditActivity.EXTRA_TEST_RESULT, batch.getTestResult());
            intent.putExtra(AdminTraceEditActivity.EXTRA_REPORT_URL, batch.getReportUrl());
        }
        startActivity(intent);
    }
}
