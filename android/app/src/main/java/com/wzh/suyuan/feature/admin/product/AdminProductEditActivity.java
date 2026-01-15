package com.wzh.suyuan.feature.admin.product;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.AdminProductRequest;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

import java.math.BigDecimal;

public class AdminProductEditActivity extends BaseActivity<AdminProductEditContract.View, AdminProductEditPresenter>
        implements AdminProductEditContract.View {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    public static final String EXTRA_PRODUCT_NAME = "extra_product_name";
    public static final String EXTRA_PRODUCT_PRICE = "extra_product_price";
    public static final String EXTRA_PRODUCT_STOCK = "extra_product_stock";
    public static final String EXTRA_PRODUCT_ORIGIN = "extra_product_origin";
    public static final String EXTRA_PRODUCT_COVER = "extra_product_cover";
    public static final String EXTRA_PRODUCT_STATUS = "extra_product_status";
    public static final String EXTRA_PRODUCT_DESC = "extra_product_desc";

    private EditText nameInput;
    private EditText priceInput;
    private EditText stockInput;
    private EditText originInput;
    private EditText coverInput;
    private EditText descInput;
    private Switch statusSwitch;
    private Button saveButton;

    private long productId;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_admin_product_edit;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        nameInput = findViewById(R.id.admin_product_input_name);
        priceInput = findViewById(R.id.admin_product_input_price);
        stockInput = findViewById(R.id.admin_product_input_stock);
        originInput = findViewById(R.id.admin_product_input_origin);
        coverInput = findViewById(R.id.admin_product_input_cover);
        descInput = findViewById(R.id.admin_product_input_desc);
        statusSwitch = findViewById(R.id.admin_product_status_switch);
        saveButton = findViewById(R.id.admin_product_save);

        if (getIntent() != null) {
            productId = getIntent().getLongExtra(EXTRA_PRODUCT_ID, 0L);
            nameInput.setText(getIntent().getStringExtra(EXTRA_PRODUCT_NAME));
            priceInput.setText(getIntent().getStringExtra(EXTRA_PRODUCT_PRICE));
            int stock = getIntent().getIntExtra(EXTRA_PRODUCT_STOCK, 0);
            stockInput.setText(String.valueOf(stock));
            originInput.setText(getIntent().getStringExtra(EXTRA_PRODUCT_ORIGIN));
            coverInput.setText(getIntent().getStringExtra(EXTRA_PRODUCT_COVER));
            descInput.setText(getIntent().getStringExtra(EXTRA_PRODUCT_DESC));
            String status = getIntent().getStringExtra(EXTRA_PRODUCT_STATUS);
            statusSwitch.setChecked(status == null || "online".equalsIgnoreCase(status));
        }

        saveButton.setOnClickListener(v -> submit());
    }

    @Override
    protected void initData() {
        if (presenter != null && productId > 0) {
            presenter.loadProduct(this, productId);
        }
    }

    @Override
    protected AdminProductEditPresenter createPresenter() {
        return new AdminProductEditPresenter();
    }

    @Override
    public void showLoading(boolean loading) {
        saveButton.setEnabled(!loading);
    }

    @Override
    public void showProduct(com.wzh.suyuan.network.model.Product product) {
        if (product == null) {
            return;
        }
        if (TextUtils.isEmpty(nameInput.getText())) {
            nameInput.setText(product.getName());
        }
        if (TextUtils.isEmpty(priceInput.getText()) && product.getPrice() != null) {
            priceInput.setText(product.getPrice().toPlainString());
        }
        if (TextUtils.isEmpty(stockInput.getText()) && product.getStock() != null) {
            stockInput.setText(String.valueOf(product.getStock()));
        }
        if (TextUtils.isEmpty(originInput.getText())) {
            originInput.setText(product.getOrigin());
        }
        if (TextUtils.isEmpty(coverInput.getText())) {
            coverInput.setText(product.getCoverUrl());
        }
        if (TextUtils.isEmpty(descInput.getText())) {
            descInput.setText(product.getDescription());
        }
        String status = product.getStatus();
        statusSwitch.setChecked(status == null || "online".equalsIgnoreCase(status));
    }

    @Override
    public void onSaveSuccess() {
        ToastUtils.showToast(getString(R.string.admin_product_save_success));
        finish();
    }

    @Override
    public void showError(String message) {
        if (!TextUtils.isEmpty(message)) {
            ToastUtils.showToast(message);
        }
    }

    private void submit() {
        String name = nameInput.getText().toString().trim();
        String priceText = priceInput.getText().toString().trim();
        String stockText = stockInput.getText().toString().trim();
        if (name.isEmpty()) {
            ToastUtils.showToast(getString(R.string.admin_product_name_required));
            return;
        }
        if (priceText.isEmpty()) {
            ToastUtils.showToast(getString(R.string.admin_product_price_required));
            return;
        }
        if (stockText.isEmpty()) {
            ToastUtils.showToast(getString(R.string.admin_product_stock_required));
            return;
        }
        BigDecimal price;
        int stock;
        try {
            price = new BigDecimal(priceText);
        } catch (NumberFormatException ex) {
            ToastUtils.showToast(getString(R.string.admin_product_price_format_error));
            return;
        }
        try {
            stock = Integer.parseInt(stockText);
        } catch (NumberFormatException ex) {
            ToastUtils.showToast(getString(R.string.admin_product_stock_format_error));
            return;
        }
        if (stock < 0) {
            ToastUtils.showToast(getString(R.string.admin_product_stock_negative));
            return;
        }
        AdminProductRequest request = new AdminProductRequest();
        request.setName(name);
        request.setPrice(price);
        request.setStock(stock);
        request.setOrigin(originInput.getText().toString().trim());
        request.setCoverUrl(coverInput.getText().toString().trim());
        request.setDescription(descInput.getText().toString().trim());
        request.setStatus(statusSwitch.isChecked() ? "online" : "offline");
        if (presenter != null) {
            presenter.saveProduct(this, productId, request);
        }
    }
}
