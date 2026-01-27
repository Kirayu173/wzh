package com.wzh.suyuan.feature.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wzh.suyuan.R;
import com.wzh.suyuan.feature.product.ProductDetailActivity;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.Product;
import com.wzh.suyuan.ui.fragment.BaseFragment;

import java.util.List;

public class HomeFragment extends BaseFragment<HomeContract.View, HomePresenter>
        implements HomeContract.View {

    private static final int PAGE_SIZE = 10;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;
    private LinearLayout pagerContainer;
    private TextView pageInfo;
    private Button prevButton;
    private Button nextButton;
    private EditText searchInput;
    private Button searchButton;
    private ProductAdapter adapter;

    private int currentPage = 1;
    private int totalPages = 1;
    private boolean isLoading = false;
    private String currentKeyword = "";

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View rootView, @Nullable Bundle savedInstanceState) {
        refreshLayout = rootView.findViewById(R.id.home_refresh);
        refreshLayout.setColorSchemeResources(R.color.color_primary);
        recyclerView = rootView.findViewById(R.id.home_list);
        stateContainer = rootView.findViewById(R.id.home_state_container);
        stateText = rootView.findViewById(R.id.home_state_text);
        stateAction = rootView.findViewById(R.id.home_state_action);
        pagerContainer = rootView.findViewById(R.id.home_pager);
        pageInfo = rootView.findViewById(R.id.home_page_info);
        prevButton = rootView.findViewById(R.id.home_page_prev);
        nextButton = rootView.findViewById(R.id.home_page_next);
        searchInput = rootView.findViewById(R.id.home_search_input);
        searchButton = rootView.findViewById(R.id.home_search_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdapter();
        adapter.setOnProductClickListener(this::openDetail);
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(() -> loadProducts(1, true));
        stateAction.setOnClickListener(v -> loadProducts(1, true));
        searchButton.setOnClickListener(v -> {
            currentKeyword = searchInput.getText().toString().trim();
            loadProducts(1, true);
        });
        prevButton.setOnClickListener(v -> {
            if (!isLoading && currentPage > 1) {
                loadProducts(currentPage - 1, false);
            }
        });
        nextButton.setOnClickListener(v -> {
            if (!isLoading && currentPage < totalPages) {
                loadProducts(currentPage + 1, false);
            }
        });
    }

    @Override
    protected void initData() {
        loadProducts(1, true);
    }

    @Override
    protected HomePresenter createPresenter() {
        return new HomePresenter();
    }

    @Override
    public void onProductsLoaded(List<Product> products, int page, int size, long total) {
        isLoading = false;
        refreshLayout.setRefreshing(false);
        currentPage = page;
        totalPages = Math.max(1, (int) Math.ceil(total / (double) size));
        adapter.setItems(products, false);
        if (products == null || products.isEmpty()) {
            if (total == 0) {
                showState(true, getString(R.string.home_empty));
            } else {
                showState(false, null);
            }
        } else {
            showState(false, null);
        }
        updatePager(total > 0);
    }

    @Override
    public void onProductsLoadFailed(String message, boolean isRefresh) {
        isLoading = false;
        refreshLayout.setRefreshing(false);
        if (adapter.getItemCount() == 0) {
            showState(true, getString(R.string.home_error));
            updatePager(false);
        }
        if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
    }

    @Override
    public void onCachedProductsLoaded(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        adapter.setItems(products, false);
        currentPage = 1;
        totalPages = 1;
        showState(false, null);
        updatePager(false);
        ToastUtils.showToast(getString(R.string.toast_cache_used));
    }

    private void loadProducts(int page, boolean refresh) {
        if (presenter == null || getContext() == null) {
            return;
        }
        isLoading = true;
        refreshLayout.setRefreshing(true);
        presenter.loadProducts(getContext(), page, PAGE_SIZE, currentKeyword, refresh);
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

    private void openDetail(Product product) {
        if (getContext() == null || product == null || product.getId() == null) {
            return;
        }
        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }
}
