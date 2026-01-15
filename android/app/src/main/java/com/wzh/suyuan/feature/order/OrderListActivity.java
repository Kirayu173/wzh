package com.wzh.suyuan.feature.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.OrderSummary;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

import java.util.List;

public class OrderListActivity extends BaseActivity<OrderListContract.View, OrderListPresenter>
        implements OrderListContract.View {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;
    private TextView tabPending;
    private TextView tabPaid;
    private TextView tabShipped;
    private TextView tabCompleted;

    private OrderListAdapter adapter;
    private String currentStatus = OrderStatus.PENDING_PAY;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_order_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        refreshLayout = findViewById(R.id.order_refresh);
        recyclerView = findViewById(R.id.order_list);
        stateContainer = findViewById(R.id.order_state_container);
        stateText = findViewById(R.id.order_state_text);
        stateAction = findViewById(R.id.order_state_action);
        tabPending = findViewById(R.id.order_tab_pending);
        tabPaid = findViewById(R.id.order_tab_paid);
        tabShipped = findViewById(R.id.order_tab_shipped);
        tabCompleted = findViewById(R.id.order_tab_completed);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderListAdapter();
        adapter.setOnOrderClickListener(order -> {
            if (order == null || order.getId() == null) {
                return;
            }
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        tabPending.setOnClickListener(v -> switchTab(OrderStatus.PENDING_PAY, tabPending));
        tabPaid.setOnClickListener(v -> switchTab(OrderStatus.PAID, tabPaid));
        tabShipped.setOnClickListener(v -> switchTab(OrderStatus.SHIPPED, tabShipped));
        tabCompleted.setOnClickListener(v -> switchTab(OrderStatus.COMPLETED, tabCompleted));
        refreshLayout.setOnRefreshListener(this::loadOrders);
        stateAction.setOnClickListener(v -> loadOrders());
    }

    @Override
    protected void initData() {
        updateTabState(tabPending);
        loadOrders();
    }

    @Override
    protected OrderListPresenter createPresenter() {
        return new OrderListPresenter();
    }

    @Override
    public void showLoading(boolean loading) {
        refreshLayout.setRefreshing(loading);
    }

    @Override
    public void showOrders(List<OrderSummary> orders) {
        adapter.setItems(orders);
        showState(false, null);
    }

    @Override
    public void showCachedOrders(List<OrderSummary> orders) {
        adapter.setItems(orders);
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
    }

    private void switchTab(String status, TextView target) {
        if (status == null || status.equals(currentStatus)) {
            return;
        }
        currentStatus = status;
        updateTabState(target);
        loadOrders();
    }

    private void loadOrders() {
        if (presenter != null) {
            presenter.loadOrders(this, currentStatus);
        }
    }

    private void updateTabState(TextView selected) {
        applyTabState(tabPending, tabPending == selected);
        applyTabState(tabPaid, tabPaid == selected);
        applyTabState(tabShipped, tabShipped == selected);
        applyTabState(tabCompleted, tabCompleted == selected);
    }

    private void applyTabState(TextView tab, boolean selected) {
        if (tab == null) {
            return;
        }
        tab.setSelected(selected);
        tab.setBackgroundResource(selected ? R.drawable.bg_primary_button : android.R.color.transparent);
        tab.setTextColor(getResources().getColor(selected ? android.R.color.white : R.color.text_secondary));
    }

    private void showState(boolean show, String message) {
        stateContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        if (message != null) {
            stateText.setText(message);
        }
    }
}
