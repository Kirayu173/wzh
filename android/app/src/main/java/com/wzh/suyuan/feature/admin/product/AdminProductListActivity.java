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
    private LinearLayout pagerContainer;
    private TextView pageInfo;
    private Button prevButton;
    private Button nextButton;
    private EditText searchInput;
    private Button searchButton;
    private AdminProductAdapter adapter;

    private static final int PAGE_SIZE = 10;
    private int currentPage = 1;
    private int totalPages = 1;
    private String currentKeyword = "";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_admin_product_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        refreshLayout = findViewById(R.id.admin_product_refresh);
        refreshLayout.setColorSchemeResources(R.color.color_primary);
        recyclerView = findViewById(R.id.admin_product_list);
        stateContainer = findViewById(R.id.admin_product_state_container);
        stateText = findViewById(R.id.admin_product_state_text);
        stateAction = findViewById(R.id.admin_product_state_action);
        addButton = findViewById(R.id.admin_product_add);
        pagerContainer = findViewById(R.id.admin_product_pager);
        pageInfo = findViewById(R.id.admin_product_page_info);
        prevButton = findViewById(R.id.admin_product_page_prev);
        nextButton = findViewById(R.id.admin_product_page_next);
        searchInput = findViewById(R.id.admin_product_search_input);
        searchButton = findViewById(R.id.admin_product_search_button);

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
                presenter.updateStatus(AdminProductListActivity.this, product, next,
                        currentPage, PAGE_SIZE, currentKeyword);
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
                                presenter.deleteProduct(AdminProductListActivity.this, product,
                                        currentPage, PAGE_SIZE, currentKeyword);
                            }
                        })
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> openEdit(null));
        refreshLayout.setOnRefreshListener(() -> loadProducts(1));
        stateAction.setOnClickListener(v -> loadProducts(1));
        searchButton.setOnClickListener(v -> {
            currentKeyword = searchInput.getText().toString().trim();
            loadProducts(1);
        });
        prevButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                loadProducts(currentPage - 1);
            }
        });
        nextButton.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                loadProducts(currentPage + 1);
            }
        });
    }

    @Override
    protected void initData() {
        loadProducts(1);
    }

    @Override
    protected AdminProductListPresenter createPresenter() {
        return new AdminProductListPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts(currentPage);
    }

    @Override
    public void showLoading(boolean loading) {
        refreshLayout.setRefreshing(loading);
    }

    @Override
    public void showProducts(List<Product> products, int page, int size, long total) {
        adapter.setItems(products);
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

    private void loadProducts(int page) {
        if (presenter != null) {
            presenter.loadProducts(this, page, PAGE_SIZE, currentKeyword);
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
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.admin_product_update_stock)
                .setView(view)
                .create();
        Button cancelButton = view.findViewById(R.id.admin_stock_cancel);
        Button confirmButton = view.findViewById(R.id.admin_stock_confirm);
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        confirmButton.setOnClickListener(v -> {
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
                presenter.updateStock(AdminProductListActivity.this, product, stock,
                        currentPage, PAGE_SIZE, currentKeyword);
                dialog.dismiss();
            } catch (NumberFormatException ex) {
                ToastUtils.showToast(getString(R.string.admin_product_stock_format_error));
            }
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
