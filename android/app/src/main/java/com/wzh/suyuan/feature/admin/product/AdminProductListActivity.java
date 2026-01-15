package com.wzh.suyuan.feature.admin.product;

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
import com.wzh.suyuan.network.model.Product;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

import java.util.List;

public class AdminProductListActivity extends BaseActivity<AdminProductListContract.View, AdminProductListPresenter>
        implements AdminProductListContract.View {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;
    private Button addButton;
    private AdminProductAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_admin_product_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        refreshLayout = findViewById(R.id.admin_product_refresh);
        recyclerView = findViewById(R.id.admin_product_list);
        stateContainer = findViewById(R.id.admin_product_state_container);
        stateText = findViewById(R.id.admin_product_state_text);
        stateAction = findViewById(R.id.admin_product_state_action);
        addButton = findViewById(R.id.admin_product_add);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProductAdapter();
        adapter.setProductActionListener(new AdminProductAdapter.ProductActionListener() {
            @Override
            public void onEdit(Product product) {
                openEdit(product);
            }

            @Override
            public void onToggleStatus(Product product) {
                if (presenter == null || product == null) {
                    return;
                }
                String status = product.getStatus();
                String next = "online".equalsIgnoreCase(status) ? "offline" : "online";
                presenter.updateStatus(AdminProductListActivity.this, product, next);
            }

            @Override
            public void onUpdateStock(Product product) {
                showStockDialog(product);
            }

            @Override
            public void onDelete(Product product) {
                if (product == null) {
                    return;
                }
                new AlertDialog.Builder(AdminProductListActivity.this)
                        .setTitle(R.string.action_delete)
                        .setMessage(R.string.admin_product_delete_confirm)
                        .setNegativeButton(R.string.action_cancel, null)
                        .setPositiveButton(R.string.action_delete, (dialog, which) -> {
                            if (presenter != null) {
                                presenter.deleteProduct(AdminProductListActivity.this, product);
                            }
                        })
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> openEdit(null));
        refreshLayout.setOnRefreshListener(this::loadProducts);
        stateAction.setOnClickListener(v -> loadProducts());
    }

    @Override
    protected void initData() {
        loadProducts();
    }

    @Override
    protected AdminProductListPresenter createPresenter() {
        return new AdminProductListPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    @Override
    public void showLoading(boolean loading) {
        refreshLayout.setRefreshing(loading);
    }

    @Override
    public void showProducts(List<Product> products) {
        adapter.setItems(products);
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

    private void loadProducts() {
        if (presenter != null) {
            presenter.loadProducts(this);
        }
    }

    private void openEdit(Product product) {
        Intent intent = new Intent(this, AdminProductEditActivity.class);
        if (product != null) {
            intent.putExtra(AdminProductEditActivity.EXTRA_PRODUCT_ID, product.getId());
            intent.putExtra(AdminProductEditActivity.EXTRA_PRODUCT_NAME, product.getName());
            intent.putExtra(AdminProductEditActivity.EXTRA_PRODUCT_PRICE, product.getPrice() == null ? "" : product.getPrice().toPlainString());
            intent.putExtra(AdminProductEditActivity.EXTRA_PRODUCT_STOCK, product.getStock() == null ? 0 : product.getStock());
            intent.putExtra(AdminProductEditActivity.EXTRA_PRODUCT_ORIGIN, product.getOrigin());
            intent.putExtra(AdminProductEditActivity.EXTRA_PRODUCT_COVER, product.getCoverUrl());
            intent.putExtra(AdminProductEditActivity.EXTRA_PRODUCT_STATUS, product.getStatus());
            intent.putExtra(AdminProductEditActivity.EXTRA_PRODUCT_DESC, product.getDescription());
        }
        startActivity(intent);
    }

    private void showStockDialog(Product product) {
        if (product == null) {
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.dialog_admin_stock, null);
        EditText input = view.findViewById(R.id.admin_stock_input);
        input.setText(String.valueOf(product.getStock() == null ? 0 : product.getStock()));
        new AlertDialog.Builder(this)
                .setTitle(R.string.admin_product_update_stock)
                .setView(view)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_save, (dialog, which) -> {
                    if (presenter == null) {
                        return;
                    }
                    String text = input.getText().toString().trim();
                    if (text.isEmpty()) {
                        ToastUtils.showToast(getString(R.string.admin_product_stock_required));
                        return;
                    }
                    try {
                        int stock = Integer.parseInt(text);
                        if (stock < 0) {
                            ToastUtils.showToast(getString(R.string.admin_product_stock_negative));
                            return;
                        }
                        presenter.updateStock(AdminProductListActivity.this, product, stock);
                    } catch (NumberFormatException ex) {
                        ToastUtils.showToast(getString(R.string.admin_product_stock_format_error));
                    }
                })
                .show();
    }
}
