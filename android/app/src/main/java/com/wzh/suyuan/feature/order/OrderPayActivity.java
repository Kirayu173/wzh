package com.wzh.suyuan.feature.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.OrderDetail;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

public class OrderPayActivity extends BaseActivity<OrderPayContract.View, OrderPayPresenter>
        implements OrderPayContract.View {

    public static final String EXTRA_ORDER_ID = "extra_order_id";
    public static final String EXTRA_ORDER_AMOUNT = "extra_order_amount";

    private TextView orderIdView;
    private TextView amountView;
    private Button payButton;

    private long orderId;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_order_pay;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        orderIdView = findViewById(R.id.order_pay_id);
        amountView = findViewById(R.id.order_pay_amount);
        payButton = findViewById(R.id.order_pay_action);

        orderId = getIntent().getLongExtra(EXTRA_ORDER_ID, 0L);
        String amount = getIntent().getStringExtra(EXTRA_ORDER_AMOUNT);
        orderIdView.setText(getString(R.string.order_number_value, String.valueOf(orderId)));
        amountView.setText("гд" + (amount == null ? "0.00" : amount));
        payButton.setEnabled(orderId > 0);
        payButton.setOnClickListener(v -> {
            if (presenter != null) {
                presenter.payOrder(this, orderId);
            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    protected OrderPayPresenter createPresenter() {
        return new OrderPayPresenter();
    }

    @Override
    public void showLoading(boolean loading) {
        payButton.setEnabled(!loading);
    }

    @Override
    public void onPaySuccess(OrderDetail detail) {
        ToastUtils.showToast(getString(R.string.order_pay_success));
        long targetId = detail != null && detail.getId() != null ? detail.getId() : orderId;
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, targetId);
        startActivity(intent);
        finish();
    }

    @Override
    public void showError(String message) {
        if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
    }
}
