package com.wzh.suyuan.feature.trace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import com.wzh.suyuan.R;
import com.wzh.suyuan.data.db.entity.ScanRecordEntity;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

public class TraceRecordActivity extends BaseActivity<TraceRecordContract.View, TraceRecordPresenter>
        implements TraceRecordContract.View {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;

    private TraceRecordAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_trace_record;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        refreshLayout = findViewById(R.id.trace_record_refresh);
        recyclerView = findViewById(R.id.trace_record_list);
        stateContainer = findViewById(R.id.trace_record_state);
        stateText = findViewById(R.id.trace_record_state_text);
        stateAction = findViewById(R.id.trace_record_state_action);

        adapter = new TraceRecordAdapter();
        adapter.setOnRecordClickListener(this::openDetail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this::loadRecords);
        stateAction.setOnClickListener(v -> loadRecords());
    }

    @Override
    protected void initData() {
        loadRecords();
    }

    @Override
    protected TraceRecordPresenter createPresenter() {
        return new TraceRecordPresenter();
    }

    @Override
    public void showLoading(boolean loading) {
        refreshLayout.setRefreshing(loading);
    }

    @Override
    public void showRecords(List<ScanRecordEntity> records) {
        adapter.setItems(records);
        showState(false, null);
    }

    @Override
    public void showEmpty(String message) {
        adapter.setItems(null);
        showState(true, message);
    }

    @Override
    public void showError(String message) {
        if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
        if (adapter.getItemCount() == 0) {
            showState(true, message);
        }
    }

    private void loadRecords() {
        if (presenter != null) {
            presenter.loadRecords(this);
        }
    }

    private void showState(boolean show, String message) {
        stateContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        if (message != null) {
            stateText.setText(message);
        }
    }

    private void openDetail(ScanRecordEntity record) {
        if (record == null || record.getTraceCode() == null) {
            return;
        }
        Intent intent = new Intent(this, TraceDetailActivity.class);
        intent.putExtra(TraceDetailActivity.EXTRA_TRACE_CODE, record.getTraceCode());
        startActivity(intent);
    }
}
