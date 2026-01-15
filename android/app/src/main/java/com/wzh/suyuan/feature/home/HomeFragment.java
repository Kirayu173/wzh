package com.wzh.suyuan.feature.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private ProductAdapter adapter;

    private int currentPage = 1;
    private boolean hasMore = true;
    private boolean isLoading = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View rootView, @Nullable Bundle savedInstanceState) {
        refreshLayout = rootView.findViewById(R.id.home_refresh);
        recyclerView = rootView.findViewById(R.id.home_list);
        stateContainer = rootView.findViewById(R.id.home_state_container);
        stateText = rootView.findViewById(R.id.home_state_text);
        stateAction = rootView.findViewById(R.id.home_state_action);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ProductAdapter();
        adapter.setOnProductClickListener(this::openDetail);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@androidx.annotation.NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0) {
                    return;
                }
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                if (!isLoading && hasMore && lastVisible >= adapter.getItemCount() - 2) {
                    loadProducts(false);
                }
            }
        });

        refreshLayout.setOnRefreshListener(() -> loadProducts(true));
        stateAction.setOnClickListener(v -> loadProducts(true));
    }

    @Override
    protected void initData() {
        loadProducts(true);
    }

    @Override
    protected HomePresenter createPresenter() {
        return new HomePresenter();
    }

    @Override
    public void onProductsLoaded(List<Product> products, boolean isRefresh, boolean hasMore) {
        this.hasMore = hasMore;
        isLoading = false;
        refreshLayout.setRefreshing(false);
        adapter.setItems(products, !isRefresh);
        if (isRefresh) {
            currentPage = 1;
        }
        if (!products.isEmpty()) {
            showState(false, null);
            currentPage++;
            return;
        }
        if (adapter.getItemCount() == 0) {
            showState(true, getString(R.string.home_empty));
        }
    }

    @Override
    public void onProductsLoadFailed(String message, boolean isRefresh) {
        isLoading = false;
        refreshLayout.setRefreshing(false);
        if (adapter.getItemCount() == 0) {
            showState(true, getString(R.string.home_error));
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
        showState(false, null);
        ToastUtils.showToast(getString(R.string.toast_cache_used));
    }

    private void loadProducts(boolean refresh) {
        if (presenter == null || getContext() == null) {
            return;
        }
        if (refresh) {
            currentPage = 1;
            hasMore = true;
        }
        if (!hasMore && !refresh) {
            return;
        }
        isLoading = true;
        refreshLayout.setRefreshing(refresh);
        presenter.loadProducts(getContext(), currentPage, PAGE_SIZE, refresh);
    }

    private void showState(boolean show, String message) {
        stateContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        if (message != null) {
            stateText.setText(message);
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
