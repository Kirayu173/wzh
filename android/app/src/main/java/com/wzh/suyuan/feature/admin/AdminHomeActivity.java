package com.wzh.suyuan.feature.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.wzh.suyuan.R;
import com.wzh.suyuan.feature.admin.order.AdminOrderListActivity;
import com.wzh.suyuan.feature.admin.product.AdminProductListActivity;
import com.wzh.suyuan.feature.admin.trace.AdminTraceListActivity;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

public class AdminHomeActivity extends BaseActivity<AdminHomeContract.View, AdminHomePresenter>
        implements AdminHomeContract.View {

    private Button productButton;
    private Button orderButton;
    private Button traceButton;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_admin_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        productButton = findViewById(R.id.admin_entry_product);
        orderButton = findViewById(R.id.admin_entry_order);
        traceButton = findViewById(R.id.admin_entry_trace);

        productButton.setOnClickListener(v ->
                startActivity(new Intent(this, AdminProductListActivity.class)));
        orderButton.setOnClickListener(v ->
                startActivity(new Intent(this, AdminOrderListActivity.class)));
        traceButton.setOnClickListener(v ->
                startActivity(new Intent(this, AdminTraceListActivity.class)));
    }

    @Override
    protected void initData() {
    }

    @Override
    protected AdminHomePresenter createPresenter() {
        return new AdminHomePresenter();
    }
}
