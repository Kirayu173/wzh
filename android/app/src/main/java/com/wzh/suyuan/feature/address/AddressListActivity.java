package com.wzh.suyuan.feature.address;

import android.content.Intent;
import android.os.Bundle;
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
import com.wzh.suyuan.network.model.Address;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

import java.util.List;

public class AddressListActivity extends BaseActivity<AddressListContract.View, AddressListPresenter>
        implements AddressListContract.View {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;
    private Button addButton;

    private AddressListAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_address_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        refreshLayout = findViewById(R.id.address_refresh);
        refreshLayout.setColorSchemeResources(R.color.color_primary);
        recyclerView = findViewById(R.id.address_list);
        stateContainer = findViewById(R.id.address_state_container);
        stateText = findViewById(R.id.address_state_text);
        stateAction = findViewById(R.id.address_state_action);
        addButton = findViewById(R.id.address_add);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressListAdapter();
        adapter.setAddressActionListener(new AddressListAdapter.AddressActionListener() {
            @Override
            public void onSetDefault(Address address) {
                if (presenter != null) {
                    presenter.setDefault(AddressListActivity.this, address);
                }
            }

            @Override
            public void onEdit(Address address) {
                if (address == null) {
                    return;
                }
                Intent intent = new Intent(AddressListActivity.this, AddressEditActivity.class);
                intent.putExtra(AddressEditActivity.EXTRA_ADDRESS_ID, address.getId());
                intent.putExtra(AddressEditActivity.EXTRA_RECEIVER, address.getReceiver());
                intent.putExtra(AddressEditActivity.EXTRA_PHONE, address.getPhone());
                intent.putExtra(AddressEditActivity.EXTRA_PROVINCE, address.getProvince());
                intent.putExtra(AddressEditActivity.EXTRA_CITY, address.getCity());
                intent.putExtra(AddressEditActivity.EXTRA_DETAIL, address.getDetail());
                intent.putExtra(AddressEditActivity.EXTRA_IS_DEFAULT, Boolean.TRUE.equals(address.getIsDefault()));
                startActivity(intent);
            }

            @Override
            public void onDelete(Address address) {
                if (address == null) {
                    return;
                }
                new AlertDialog.Builder(AddressListActivity.this)
                        .setTitle(R.string.action_delete)
                        .setMessage(R.string.address_delete_confirm)
                        .setNegativeButton(R.string.action_cancel, null)
                        .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                            if (presenter != null) {
                                presenter.deleteAddress(AddressListActivity.this, address);
                            }
                        })
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v ->
                startActivity(new Intent(AddressListActivity.this, AddressEditActivity.class)));
        refreshLayout.setOnRefreshListener(this::loadAddresses);
        stateAction.setOnClickListener(v -> loadAddresses());
    }

    @Override
    protected void initData() {
        loadAddresses();
    }

    @Override
    protected AddressListPresenter createPresenter() {
        return new AddressListPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    @Override
    public void showLoading(boolean loading) {
        refreshLayout.setRefreshing(loading);
    }

    @Override
    public void showAddresses(List<Address> addresses) {
        adapter.setItems(addresses);
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

    private void loadAddresses() {
        if (presenter != null) {
            presenter.loadAddresses(this);
        }
    }
}
