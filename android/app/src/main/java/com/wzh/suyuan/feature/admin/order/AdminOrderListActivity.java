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
    private AdminOrderListAdapter adapter;
    private String currentStatus;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_admin_order_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        refreshLayout = findViewById(R.id.admin_order_refresh);
        recyclerView = findViewById(R.id.admin_order_list);
        stateContainer = findViewById(R.id.admin_order_state_container);
        stateText = findViewById(R.id.admin_order_state_text);
        stateAction = findViewById(R.id.admin_order_state_action);
        tabAll = findViewById(R.id.admin_order_tab_all);
        tabPaid = findViewById(R.id.admin_order_tab_paid);
        tabShipped = findViewById(R.id.admin_order_tab_shipped);
        tabCompleted = findViewById(R.id.admin_order_tab_completed);

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
        refreshLayout.setOnRefreshListener(this::loadOrders);
        stateAction.setOnClickListener(v -> loadOrders());
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
    public void showOrders(List<OrderSummary> orders) {
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

    private void switchStatus(String status) {
        currentStatus = status;
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
        if (presenter != null) {
            presenter.loadOrders(this, currentStatus);
        }
    }

    private void showShipDialog(long orderId) {
        View view = getLayoutInflater().inflate(R.layout.dialog_admin_ship, null);
        EditText companyInput = view.findViewById(R.id.admin_ship_company);
        EditText noInput = view.findViewById(R.id.admin_ship_no);
        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_order_action_ship)
                .setView(view)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_save, (dialog, which) -> {
                    String company = companyInput.getText().toString().trim();
                    String expressNo = noInput.getText().toString().trim();
                    if (company.isEmpty() || expressNo.isEmpty()) {
                        ToastUtils.showToast(getString(R.string.admin_order_ship_required));
                        return;
                    }
                    if (presenter != null) {
                        presenter.shipOrder(this, orderId, company, expressNo, currentStatus);
                    }
                })
                .show();
    }
}
