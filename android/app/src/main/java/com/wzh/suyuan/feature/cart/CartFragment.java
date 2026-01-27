package com.wzh.suyuan.feature.cart;

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
import com.wzh.suyuan.data.db.entity.CartEntity;
import com.wzh.suyuan.kit.FormatUtils;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.ui.fragment.BaseFragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends BaseFragment<CartContract.View, CartPresenter>
        implements CartContract.View {

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;
    private TextView totalValueView;
    private Button checkoutButton;

    private CartAdapter adapter;
    private final List<CartEntity> items = new ArrayList<>();

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_cart;
    }

    @Override
    protected void initView(View rootView, @Nullable Bundle savedInstanceState) {
        refreshLayout = rootView.findViewById(R.id.cart_refresh);
        refreshLayout.setColorSchemeResources(R.color.color_primary);
        recyclerView = rootView.findViewById(R.id.cart_list);
        stateContainer = rootView.findViewById(R.id.cart_state_container);
        stateText = rootView.findViewById(R.id.cart_state_text);
        stateAction = rootView.findViewById(R.id.cart_state_action);
        totalValueView = rootView.findViewById(R.id.cart_total_value);
        checkoutButton = rootView.findViewById(R.id.cart_checkout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter();
        adapter.setCartActionListener(new CartAdapter.CartActionListener() {
            @Override
            public void onQuantityChanged(CartEntity item, int newQuantity) {
                int oldQuantity = item.getQuantity();
                item.setQuantity(newQuantity);
                notifyItemChanged(item);
                updateTotal();
                if (presenter != null) {
                    presenter.updateQuantity(getContext(), item, newQuantity, oldQuantity);
                }
            }

            @Override
            public void onSelectionChanged(CartEntity item, boolean selected) {
                boolean oldSelected = item.isSelected();
                item.setSelected(selected);
                notifyItemChanged(item);
                updateTotal();
                if (presenter != null) {
                    presenter.updateSelection(getContext(), item, oldSelected);
                }
            }

            @Override
            public void onDelete(CartEntity item, int position) {
                if (position >= 0 && position < items.size()) {
                    items.remove(position);
                    adapter.notifyItemRemoved(position);
                    updateTotal();
                    if (presenter != null) {
                        presenter.deleteItem(getContext(), item, position);
                    }
                    toggleEmptyState();
                }
            }
        });
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this::refreshCart);
        stateAction.setOnClickListener(v -> refreshCart());
        checkoutButton.setOnClickListener(v -> startCheckout());
    }

    @Override
    protected void initData() {
        refreshCart();
    }

    @Override
    protected CartPresenter createPresenter() {
        return new CartPresenter();
    }

    @Override
    public void onCartLoaded(List<CartEntity> data) {
        refreshLayout.setRefreshing(false);
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        adapter.setItems(items);
        updateTotal();
        toggleEmptyState();
    }

    @Override
    public void onCartLoadFailed(String message) {
        refreshLayout.setRefreshing(false);
        if (items.isEmpty()) {
            showState(true, getString(R.string.cart_error));
        }
        if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
    }

    @Override
    public void onCartItemUpdateFailed(CartEntity item, int oldQuantity, String message) {
        item.setQuantity(oldQuantity);
        notifyItemChanged(item);
        updateTotal();
        if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
    }

    @Override
    public void onCartItemSelectionFailed(CartEntity item, boolean oldSelected, String message) {
        item.setSelected(oldSelected);
        notifyItemChanged(item);
        updateTotal();
        if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
    }

    @Override
    public void onCartItemDeleteFailed(CartEntity item, int position, String message) {
        if (position < 0) {
            position = 0;
        }
        if (position > items.size()) {
            position = items.size();
        }
        items.add(position, item);
        adapter.notifyItemInserted(position);
        updateTotal();
        if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
    }

    @Override
    public void showError(String message) {
        if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
    }

    private void refreshCart() {
        if (presenter == null) {
            return;
        }
        refreshLayout.setRefreshing(true);
        presenter.loadCart(getContext());
    }

    private void updateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartEntity item : items) {
            if (!item.isSelected()) {
                continue;
            }
            BigDecimal price = item.getPriceSnapshot() == null ? BigDecimal.ZERO : item.getPriceSnapshot();
            total = total.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        totalValueView.setText(getString(R.string.price_value, FormatUtils.formatPrice(total)));
        checkoutButton.setEnabled(hasSelectedItems());
    }

    private void toggleEmptyState() {
        if (items.isEmpty()) {
            showState(true, getString(R.string.cart_empty));
        } else {
            showState(false, null);
        }
    }

    private void showState(boolean show, String message) {
        stateContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        if (message != null) {
            stateText.setText(message);
        }
    }

    private void notifyItemChanged(CartEntity item) {
        int index = items.indexOf(item);
        if (index >= 0) {
            adapter.notifyItemChanged(index);
        }
    }

    private boolean hasSelectedItems() {
        for (CartEntity item : items) {
            if (item.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private void startCheckout() {
        if (!hasSelectedItems()) {
            ToastUtils.showToast(getString(R.string.order_confirm_empty));
            return;
        }
        if (getContext() == null) {
            return;
        }
        startActivity(new android.content.Intent(getContext(),
                com.wzh.suyuan.feature.order.OrderConfirmActivity.class));
    }
}
