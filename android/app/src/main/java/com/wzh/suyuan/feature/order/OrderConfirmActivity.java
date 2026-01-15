package com.wzh.suyuan.feature.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wzh.suyuan.R;
import com.wzh.suyuan.feature.address.AddressListActivity;
import com.wzh.suyuan.kit.FormatUtils;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.Address;
import com.wzh.suyuan.network.model.CartItem;
import com.wzh.suyuan.network.model.OrderCreateResponse;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

import java.math.BigDecimal;
import java.util.List;

public class OrderConfirmActivity extends BaseActivity<OrderConfirmContract.View, OrderConfirmPresenter>
        implements OrderConfirmContract.View {

    private TextView addressNameView;
    private TextView addressDetailView;
    private Button manageAddressButton;
    private RecyclerView recyclerView;
    private EditText memoInput;
    private TextView totalView;
    private Button submitButton;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;

    private OrderConfirmAdapter adapter;
    private Address selectedAddress;
    private String orderRequestId;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_order_confirm;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        addressNameView = findViewById(R.id.order_address_name);
        addressDetailView = findViewById(R.id.order_address_detail);
        manageAddressButton = findViewById(R.id.order_manage_address);
        recyclerView = findViewById(R.id.order_confirm_list);
        memoInput = findViewById(R.id.order_memo_input);
        totalView = findViewById(R.id.order_confirm_total);
        submitButton = findViewById(R.id.order_submit);
        stateContainer = findViewById(R.id.order_confirm_state);
        stateText = findViewById(R.id.order_confirm_state_text);
        stateAction = findViewById(R.id.order_confirm_state_action);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderConfirmAdapter();
        recyclerView.setAdapter(adapter);

        manageAddressButton.setOnClickListener(v ->
                startActivity(new Intent(this, AddressListActivity.class)));
        submitButton.setOnClickListener(v -> submitOrder());
        submitButton.setEnabled(false);
        stateAction.setOnClickListener(v -> refreshData());
    }

    @Override
    protected void initData() {
        refreshData();
    }

    @Override
    protected OrderConfirmPresenter createPresenter() {
        return new OrderConfirmPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void showLoading(boolean loading) {
        submitButton.setEnabled(!loading);
    }

    @Override
    public void showAddress(Address address) {
        selectedAddress = address;
        if (address == null) {
            addressNameView.setText(getString(R.string.address_none_hint));
            addressDetailView.setText(getString(R.string.address_none_action));
        } else {
            String receiver = address.getReceiver() == null ? "" : address.getReceiver();
            String phone = address.getPhone() == null ? "" : address.getPhone();
            addressNameView.setText(getString(R.string.address_name_phone, receiver, phone));
            addressDetailView.setText(buildAddress(address));
        }
        updateSubmitState();
    }

    @Override
    public void showCartItems(List<CartItem> items) {
        adapter.setItems(items);
        updateTotal(items);
        showState(false, null);
        updateSubmitState();
    }

    @Override
    public void showEmpty(String message) {
        adapter.setItems(null);
        updateTotal(null);
        showState(true, message);
        updateSubmitState();
    }

    @Override
    public void onOrderCreated(OrderCreateResponse response) {
        if (response == null || response.getId() == null) {
            ToastUtils.showToast(getString(R.string.order_create_failed));
            return;
        }
        orderRequestId = null;
        Intent intent = new Intent(this, OrderPayActivity.class);
        intent.putExtra(OrderPayActivity.EXTRA_ORDER_ID, response.getId());
        if (response.getTotalAmount() != null) {
            intent.putExtra(OrderPayActivity.EXTRA_ORDER_AMOUNT,
                    FormatUtils.formatPrice(response.getTotalAmount()));
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void showError(String message) {
        if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
    }

    private void submitOrder() {
        if (selectedAddress == null || selectedAddress.getId() == null) {
            ToastUtils.showToast(getString(R.string.address_none_action));
            return;
        }
        List<CartItem> items = adapter.getItems();
        if (items == null || items.isEmpty()) {
            ToastUtils.showToast(getString(R.string.order_confirm_empty));
            return;
        }
        String memo = memoInput.getText().toString().trim();
        if (presenter != null) {
            if (orderRequestId == null) {
                orderRequestId = java.util.UUID.randomUUID().toString();
            }
            presenter.createOrder(this, selectedAddress.getId(), memo, orderRequestId, items);
        }
    }

    private void refreshData() {
        if (presenter != null) {
            presenter.loadAddresses(this);
            presenter.loadCartItems(this);
        }
    }

    private void updateTotal(List<CartItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        if (items != null) {
            for (CartItem item : items) {
                if (item.getPriceSnapshot() == null || item.getQuantity() == null) {
                    continue;
                }
                total = total.add(item.getPriceSnapshot()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        totalView.setText(getString(R.string.price_value, FormatUtils.formatPrice(total)));
    }

    private void updateSubmitState() {
        boolean hasItems = adapter.getItems() != null && !adapter.getItems().isEmpty();
        boolean hasAddress = selectedAddress != null && selectedAddress.getId() != null;
        submitButton.setEnabled(hasItems && hasAddress);
    }

    private void showState(boolean show, String message) {
        stateContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        if (message != null) {
            stateText.setText(message);
        }
    }

    private String buildAddress(Address address) {
        StringBuilder sb = new StringBuilder();
        if (address.getProvince() != null && !address.getProvince().isEmpty()) {
            sb.append(address.getProvince());
        }
        if (address.getCity() != null && !address.getCity().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(address.getCity());
        }
        if (address.getDetail() != null && !address.getDetail().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(address.getDetail());
        }
        return sb.toString();
    }
}
