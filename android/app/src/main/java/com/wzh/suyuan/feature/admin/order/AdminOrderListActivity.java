package com.wzh.suyuan.feature.admin.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.OrderSummary;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

import java.util.List;

public class AdminOrderListActivity extends BaseActivity<AdminOrderListContract.View, AdminOrderListPresenter>
        implements AdminOrderListContract.View {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;
    private TextView tabAll;
    private TextView tabPaid;
    private TextView tabShipped;
    private TextView tabCompleted;
    private LinearLayout pagerContainer;
    private TextView pageInfo;
    private Button prevButton;
    private Button nextButton;
    private AdminOrderListAdapter adapter;
    private String currentStatus;
    private int currentPage = 1;
    private int totalPages = 1;

    private static final int PAGE_SIZE = 10;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_admin_order_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        refreshLayout = findViewById(R.id.admin_order_refresh);
        refreshLayout.setColorSchemeResources(R.color.color_primary);
        recyclerView = findViewById(R.id.admin_order_list);
        stateContainer = findViewById(R.id.admin_order_state_container);
        stateText = findViewById(R.id.admin_order_state_text);
        stateAction = findViewById(R.id.admin_order_state_action);
        tabAll = findViewById(R.id.admin_order_tab_all);
        tabPaid = findViewById(R.id.admin_order_tab_paid);
        tabShipped = findViewById(R.id.admin_order_tab_shipped);
        tabCompleted = findViewById(R.id.admin_order_tab_completed);
        pagerContainer = findViewById(R.id.admin_order_pager);
        pageInfo = findViewById(R.id.admin_order_page_info);
        prevButton = findViewById(R.id.admin_order_page_prev);
        nextButton = findViewById(R.id.admin_order_page_next);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrderListAdapter();
        adapter.setOrderActionListener(new AdminOrderListAdapter.OrderActionListener() {
            @Override
            public void onDetail(OrderSummary order) {
                if (order == null || order.getId() == null) {
                    return;
                }
                Intent intent = new Intent(AdminOrderListActivity.this, AdminOrderDetailActivity.class);
                intent.putExtra(AdminOrderDetailActivity.EXTRA_ORDER_ID, order.getId());
                startActivity(intent);
            }

            @Override
            public void onShip(OrderSummary order) {
                if (order == null || order.getId() == null) {
                    return;
                }
                showShipDialog(order.getId());
            }
        });
        recyclerView.setAdapter(adapter);

        tabAll.setOnClickListener(v -> switchStatus(null));
        tabPaid.setOnClickListener(v -> switchStatus("PAID"));
        tabShipped.setOnClickListener(v -> switchStatus("SHIPPED"));
        tabCompleted.setOnClickListener(v -> switchStatus("COMPLETED"));
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
        switchStatus(null);
    }

    @Override
    protected AdminOrderListPresenter createPresenter() {
        return new AdminOrderListPresenter();
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
    public void showEmpty(String message) {
        adapter.setItems(null);
        showState(true, message);
        updatePager(false);
    }

    @Override
    public void showError(String message) {
        if (!TextUtils.isEmpty(message)) {
            ToastUtils.showToast(message);
        }
        if (adapter != null && adapter.getItemCount() == 0) {
            showState(true, message);
            updatePager(false);
        }
    }

    private void showState(boolean show, String message) {
        stateContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        if (message != null) {
            stateText.setText(message);
        }
    }

    private void switchStatus(String status) {
        currentStatus = status;
        currentPage = 1;
        updateTabStyle();
        loadOrders();
    }

    private void updateTabStyle() {
        tabAll.setSelected(currentStatus == null);
        tabPaid.setSelected("PAID".equals(currentStatus));
        tabShipped.setSelected("SHIPPED".equals(currentStatus));
        tabCompleted.setSelected("COMPLETED".equals(currentStatus));
    }

    private void loadOrders() {
        loadOrders(currentPage);
    }

    private void loadOrders(int page) {
        if (presenter != null) {
            presenter.loadOrders(this, currentStatus, page, PAGE_SIZE);
        }
    }

    private void showShipDialog(long orderId) {
        View view = getLayoutInflater().inflate(R.layout.dialog_admin_ship, null);
        EditText companyInput = view.findViewById(R.id.admin_ship_company);
        EditText noInput = view.findViewById(R.id.admin_ship_no);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.admin_order_action_ship)
                .setView(view)
                .create();
        Button cancelButton = view.findViewById(R.id.admin_ship_cancel);
        Button confirmButton = view.findViewById(R.id.admin_ship_confirm);
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        confirmButton.setOnClickListener(v -> {
            String company = companyInput.getText().toString().trim();
            String expressNo = noInput.getText().toString().trim();
            if (company.isEmpty() || expressNo.isEmpty()) {
                ToastUtils.showToast(getString(R.string.admin_order_ship_required));
                return;
            }
            if (presenter != null) {
                presenter.shipOrder(this, orderId, company, expressNo,
                        currentStatus, currentPage, PAGE_SIZE);
            }
            dialog.dismiss();
        });
        dialog.show();
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
