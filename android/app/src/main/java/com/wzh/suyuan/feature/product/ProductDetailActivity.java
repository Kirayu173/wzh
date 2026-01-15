package com.wzh.suyuan.feature.product;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.FormatUtils;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.Product;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends BaseActivity<ProductDetailContract.View, ProductDetailPresenter>
        implements ProductDetailContract.View {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    private ViewPager2 viewPager;
    private TextView indicatorView;
    private TextView nameView;
    private TextView priceView;
    private TextView stockView;
    private TextView originView;
    private TextView descView;
    private TextView quantityView;
    private TextView minusView;
    private TextView plusView;
    private Button addCartButton;
    private LinearLayout stateContainer;
    private TextView stateText;
    private Button stateAction;
    private ProgressBar progressBar;

    private ProductImageAdapter imageAdapter;
    private Product currentProduct;
    private int quantity = 1;
    private long productId;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_product_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        viewPager = findViewById(R.id.detail_viewpager);
        indicatorView = findViewById(R.id.detail_indicator);
        nameView = findViewById(R.id.detail_name);
        priceView = findViewById(R.id.detail_price);
        stockView = findViewById(R.id.detail_stock);
        originView = findViewById(R.id.detail_origin);
        descView = findViewById(R.id.detail_description);
        quantityView = findViewById(R.id.detail_quantity);
        minusView = findViewById(R.id.detail_minus);
        plusView = findViewById(R.id.detail_plus);
        addCartButton = findViewById(R.id.button_add_cart);
        stateContainer = findViewById(R.id.detail_state_container);
        stateText = findViewById(R.id.detail_state_text);
        stateAction = findViewById(R.id.detail_state_action);
        progressBar = findViewById(R.id.detail_progress);

        imageAdapter = new ProductImageAdapter();
        viewPager.setAdapter(imageAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicator(position + 1, imageAdapter.getImageCount());
            }
        });

        minusView.setOnClickListener(v -> updateQuantity(quantity - 1));
        plusView.setOnClickListener(v -> updateQuantity(quantity + 1));
        addCartButton.setOnClickListener(v -> {
            if (presenter != null && currentProduct != null) {
                presenter.addToCart(this, currentProduct, quantity);
            }
        });
        stateAction.setOnClickListener(v -> loadProduct());
        updateQuantity(quantity);
    }

    @Override
    protected void initData() {
        productId = getIntent().getLongExtra(EXTRA_PRODUCT_ID, 0L);
        loadProduct();
    }

    @Override
    protected ProductDetailPresenter createPresenter() {
        return new ProductDetailPresenter();
    }

    @Override
    public void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showProduct(Product product) {
        currentProduct = product;
        bindProduct(product);
        showState(false, null);
    }

    @Override
    public void showCachedProduct(Product product) {
        if (currentProduct == null) {
            currentProduct = product;
            bindProduct(product);
            ToastUtils.showToast(getString(R.string.toast_cache_used));
        }
    }

    @Override
    public void showError(String message) {
        if (currentProduct == null) {
            showState(true, message == null ? getString(R.string.detail_error) : message);
        } else if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
    }

    @Override
    public void onAddToCartSuccess(boolean isLocal) {
        ToastUtils.showToast(isLocal
                ? getString(R.string.toast_cart_local)
                : getString(R.string.toast_cart_added));
    }

    private void loadProduct() {
        if (presenter == null) {
            return;
        }
        if (productId <= 0) {
            showState(true, getString(R.string.detail_error));
            return;
        }
        showLoading(true);
        presenter.loadProduct(this, productId);
    }

    private void bindProduct(Product product) {
        showLoading(false);
        if (product == null) {
            return;
        }
        nameView.setText(product.getName());
        priceView.setText("¥" + FormatUtils.formatPrice(product.getPrice()));
        Integer stock = product.getStock();
        stockView.setText(getString(R.string.label_stock_value, stock == null ? 0 : stock));
        originView.setText(getString(R.string.label_origin_value, safeText(product.getOrigin())));
        descView.setText(safeText(product.getDescription()));
        List<String> images = product.getImages();
        if (images == null || images.isEmpty()) {
            images = new ArrayList<>();
            if (product.getCoverUrl() != null && !product.getCoverUrl().isEmpty()) {
                images.add(product.getCoverUrl());
            }
        }
        imageAdapter.setImages(images);
        updateIndicator(1, imageAdapter.getImageCount());
    }

    private void updateIndicator(int index, int total) {
        if (total <= 0) {
            indicatorView.setText("0/0");
        } else {
            indicatorView.setText(index + "/" + total);
        }
    }

    private void updateQuantity(int newQuantity) {
        quantity = Math.max(newQuantity, 1);
        quantityView.setText(String.valueOf(quantity));
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
