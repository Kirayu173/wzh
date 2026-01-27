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
    private LinearLayout pagerContainer;
    private TextView pageInfo;
    private Button prevButton;
    private Button nextButton;

    private OrderListAdapter adapter;
    private String currentStatus = OrderStatus.PENDING_PAY;
    private int currentPage = 1;
    private int totalPages = 1;
    private static final int PAGE_SIZE = 10;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_order_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        refreshLayout = findViewById(R.id.order_refresh);
        refreshLayout.setColorSchemeResources(R.color.color_primary);
        recyclerView = findViewById(R.id.order_list);
        stateContainer = findViewById(R.id.order_state_container);
        stateText = findViewById(R.id.order_state_text);
        stateAction = findViewById(R.id.order_state_action);
        tabPending = findViewById(R.id.order_tab_pending);
        tabPaid = findViewById(R.id.order_tab_paid);
        tabShipped = findViewById(R.id.order_tab_shipped);
        tabCompleted = findViewById(R.id.order_tab_completed);
        pagerContainer = findViewById(R.id.order_pager);
        pageInfo = findViewById(R.id.order_page_info);
        prevButton = findViewById(R.id.order_page_prev);
        nextButton = findViewById(R.id.order_page_next);

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
        refreshLayout.setOnRefreshListener(() -> loadOrders(1));
        stateAction.setOnClickListener(v -> loadOrders(1));
        prevButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                loadOrders(currentPage - 1);
            }
        });
        nextButton.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                loadOrders(currentPage + 1);
            }
        });
    }

    @Override
    protected void initData() {
        updateTabState(tabPending);
        loadOrders(1);
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
    public void showOrders(List<OrderSummary> orders, int page, int size, long total) {
        adapter.setItems(orders);
        currentPage = page;
        totalPages = Math.max(1, (int) Math.ceil(total / (double) size));
        showState(false, null);
        updatePager(total > 0);
    }

    @Override
    public void showCachedOrders(List<OrderSummary> orders) {
        adapter.setItems(orders);
        showState(false, null);
        currentPage = 1;
        totalPages = 1;
        updatePager(!orders.isEmpty());
    }

    @Override
    public void showEmpty(String message) {
        adapter.setItems(null);
        showState(true, message);
        updatePager(false);
    }

    @Override
    public void showError(String message) {
        if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
        if (adapter.getItemCount() == 0) {
            showState(true, message);
            updatePager(false);
        }
    }

    private void switchTab(String status, TextView target) {
        if (status == null || status.equals(currentStatus)) {
            return;
        }
        currentStatus = status;
        currentPage = 1;
        updateTabState(target);
        loadOrders(1);
    }

    private void loadOrders(int page) {
        if (presenter != null) {
            presenter.loadOrders(this, currentStatus, page, PAGE_SIZE);
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
    }

    private void showState(boolean show, String message) {
        stateContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        if (message != null) {
            stateText.setText(message);
        }
    }

    private void updatePager(boolean visible) {
        pagerContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (visible) {
            pageInfo.setText(getString(R.string.page_info, currentPage, totalPages));
            prevButton.setEnabled(currentPage > 1);
            nextButton.setEnabled(currentPage < totalPages);
        }
    }
}
