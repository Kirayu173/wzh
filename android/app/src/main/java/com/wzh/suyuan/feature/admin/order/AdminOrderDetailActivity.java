package com.wzh.suyuan.feature.admin.order;

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

import com.wzh.suyuan.R;
import com.wzh.suyuan.feature.order.OrderItemAdapter;
import com.wzh.suyuan.feature.order.OrderStatus;
import com.wzh.suyuan.kit.FormatUtils;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.OrderDetail;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

public class AdminOrderDetailActivity extends BaseActivity<AdminOrderDetailContract.View, AdminOrderDetailPresenter>
        implements AdminOrderDetailContract.View {

    public static final String EXTRA_ORDER_ID = "extra_order_id";

    private TextView statusView;
    private TextView orderIdView;
    private TextView orderTimeView;
    private TextView payTimeView;
    private TextView shipTimeView;
    private TextView confirmTimeView;
    private TextView expressView;
    private TextView addressNameView;
    private TextView addressDetailView;
    private TextView totalView;
    private RecyclerView recyclerView;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;
    private Button shipButton;

    private OrderItemAdapter adapter;
    private long orderId;
    private OrderDetail currentOrder;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_admin_order_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        statusView = findViewById(R.id.admin_order_detail_status);
        orderIdView = findViewById(R.id.admin_order_detail_id);
        orderTimeView = findViewById(R.id.admin_order_detail_time);
        payTimeView = findViewById(R.id.admin_order_detail_pay_time);
        shipTimeView = findViewById(R.id.admin_order_detail_ship_time);
        confirmTimeView = findViewById(R.id.admin_order_detail_confirm_time);
        expressView = findViewById(R.id.admin_order_detail_express);
        addressNameView = findViewById(R.id.admin_order_detail_address_name);
        addressDetailView = findViewById(R.id.admin_order_detail_address);
        totalView = findViewById(R.id.admin_order_detail_total);
        recyclerView = findViewById(R.id.admin_order_detail_list);
        stateContainer = findViewById(R.id.admin_order_detail_state);
        stateText = findViewById(R.id.admin_order_detail_state_text);
        stateAction = findViewById(R.id.admin_order_detail_state_action);
        shipButton = findViewById(R.id.admin_order_action_ship);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderItemAdapter();
        recyclerView.setAdapter(adapter);

        orderId = getIntent().getLongExtra(EXTRA_ORDER_ID, 0L);
        stateAction.setOnClickListener(v -> loadOrder());
        shipButton.setOnClickListener(v -> showShipDialog());
    }

    @Override
    protected void initData() {
        loadOrder();
    }

    @Override
    protected AdminOrderDetailPresenter createPresenter() {
        return new AdminOrderDetailPresenter();
    }

    @Override
    public void showLoading(boolean loading) {
        shipButton.setEnabled(!loading);
    }

    @Override
    public void showOrderDetail(OrderDetail detail) {
        currentOrder = detail;
        showState(false, null);
        if (detail == null) {
            return;
        }
        statusView.setText(OrderStatus.getLabel(this, detail.getStatus()));
        orderIdView.setText(getString(R.string.order_number_value, String.valueOf(detail.getId())));
        orderTimeView.setText(getString(R.string.order_time_value, safeText(detail.getCreateTime())));
        payTimeView.setText(getString(R.string.order_pay_time_value, safeText(detail.getPayTime())));
        shipTimeView.setText(getString(R.string.admin_order_ship_time_value, safeText(detail.getShipTime())));
        confirmTimeView.setText(getString(R.string.order_confirm_time_value, safeText(detail.getConfirmTime())));
        expressView.setText(getString(R.string.admin_order_express_value,
                safeText(detail.getExpressCompany()), safeText(detail.getExpressNo())));
        addressNameView.setText(getString(R.string.address_name_phone,
                safeText(detail.getReceiver()), safeText(detail.getPhone())));
        addressDetailView.setText(safeText(detail.getAddress()));
        totalView.setText(getString(R.string.order_total_value,
                getString(R.string.price_value, FormatUtils.formatPrice(detail.getTotalAmount()))));
        adapter.setItems(detail.getItems());
        shipButton.setVisibility("PAID".equalsIgnoreCase(detail.getStatus()) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showError(String message) {
        if (currentOrder == null) {
            showState(true, message == null ? getString(R.string.admin_order_error) : message);
        } else if (!TextUtils.isEmpty(message)) {
            ToastUtils.showToast(message);
        }
    }

    private void loadOrder() {
        if (presenter != null && orderId > 0) {
            presenter.loadDetail(this, orderId);
        }
    }

    private void showShipDialog() {
        if (currentOrder == null || currentOrder.getId() == null) {
            return;
        }
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
                presenter.shipOrder(this, currentOrder.getId(), company, expressNo);
            }
            dialog.dismiss();
        });
        dialog.show();
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
