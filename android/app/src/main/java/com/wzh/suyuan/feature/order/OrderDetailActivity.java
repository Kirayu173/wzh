package com.wzh.suyuan.feature.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.FormatUtils;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.OrderDetail;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

public class OrderDetailActivity extends BaseActivity<OrderDetailContract.View, OrderDetailPresenter>
        implements OrderDetailContract.View {

    public static final String EXTRA_ORDER_ID = "extra_order_id";

    private TextView statusView;
    private TextView orderIdView;
    private TextView orderTimeView;
    private TextView payTimeView;
    private TextView confirmTimeView;
    private TextView addressNameView;
    private TextView addressDetailView;
    private TextView totalView;
    private RecyclerView recyclerView;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;
    private Button payButton;
    private Button cancelButton;
    private Button confirmButton;

    private OrderItemAdapter adapter;
    private OrderDetail currentOrder;
    private long orderId;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        statusView = findViewById(R.id.order_detail_status);
        orderIdView = findViewById(R.id.order_detail_id);
        orderTimeView = findViewById(R.id.order_detail_time);
        payTimeView = findViewById(R.id.order_detail_pay_time);
        confirmTimeView = findViewById(R.id.order_detail_confirm_time);
        addressNameView = findViewById(R.id.order_detail_address_name);
        addressDetailView = findViewById(R.id.order_detail_address);
        totalView = findViewById(R.id.order_detail_total);
        recyclerView = findViewById(R.id.order_detail_list);
        stateContainer = findViewById(R.id.order_detail_state);
        stateText = findViewById(R.id.order_detail_state_text);
        stateAction = findViewById(R.id.order_detail_state_action);
        payButton = findViewById(R.id.order_action_pay);
        cancelButton = findViewById(R.id.order_action_cancel);
        confirmButton = findViewById(R.id.order_action_confirm);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderItemAdapter();
        recyclerView.setAdapter(adapter);

        orderId = getIntent().getLongExtra(EXTRA_ORDER_ID, 0L);
        stateAction.setOnClickListener(v -> loadOrder());
        payButton.setOnClickListener(v -> openPay());
        cancelButton.setOnClickListener(v -> {
            if (presenter != null) {
                presenter.cancelOrder(this, orderId);
            }
        });
        confirmButton.setOnClickListener(v -> {
            if (presenter != null) {
                presenter.confirmOrder(this, orderId);
            }
        });
    }

    @Override
    protected void initData() {
        loadOrder();
    }

    @Override
    protected OrderDetailPresenter createPresenter() {
        return new OrderDetailPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrder();
    }

    @Override
    public void showLoading(boolean loading) {
        payButton.setEnabled(!loading);
        cancelButton.setEnabled(!loading);
        confirmButton.setEnabled(!loading);
    }

    @Override
    public void showOrder(OrderDetail detail) {
        currentOrder = detail;
        showState(false, null);
        if (detail == null) {
            return;
        }
        statusView.setText(OrderStatus.getLabel(this, detail.getStatus()));
        orderIdView.setText(getString(R.string.order_number_value, String.valueOf(detail.getId())));
        orderTimeView.setText(getString(R.string.order_time_value, safeText(detail.getCreateTime())));
        payTimeView.setText(getString(R.string.order_pay_time_value, safeText(detail.getPayTime())));
        confirmTimeView.setText(getString(R.string.order_confirm_time_value, safeText(detail.getConfirmTime())));
        addressNameView.setText(getString(R.string.address_name_phone,
                safeText(detail.getReceiver()), safeText(detail.getPhone())));
        addressDetailView.setText(safeText(detail.getAddress()));
        String totalAmount = getString(R.string.price_value,
                FormatUtils.formatPrice(detail.getTotalAmount()));
        totalView.setText(getString(R.string.order_total_value, totalAmount));
        adapter.setItems(detail.getItems());
        updateActions(detail.getStatus());
    }

    @Override
    public void showError(String message) {
        if (currentOrder == null) {
            showState(true, message == null ? getString(R.string.order_error) : message);
        } else if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
    }

    private void loadOrder() {
        if (presenter != null && orderId > 0) {
            presenter.loadOrderDetail(this, orderId);
        }
    }

    private void openPay() {
        if (currentOrder == null || currentOrder.getId() == null) {
            return;
        }
        Intent intent = new Intent(this, OrderPayActivity.class);
        intent.putExtra(OrderPayActivity.EXTRA_ORDER_ID, currentOrder.getId());
        intent.putExtra(OrderPayActivity.EXTRA_ORDER_AMOUNT,
                FormatUtils.formatPrice(currentOrder.getTotalAmount()));
        startActivity(intent);
    }

    private void updateActions(String status) {
        boolean pendingPay = OrderStatus.PENDING_PAY.equals(status);
        boolean canConfirm = OrderStatus.SHIPPED.equals(status);
        payButton.setVisibility(pendingPay ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(pendingPay ? View.VISIBLE : View.GONE);
        confirmButton.setVisibility(canConfirm ? View.VISIBLE : View.GONE);
    }

    private void showState(boolean show, String message) {
        stateContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        if (message != null) {
            stateText.setText(message);
        }
    }

    private String safeText(String value) {
        return value == null || value.isEmpty() ? "-" : value;
    }
}
