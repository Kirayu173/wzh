package com.wzh.suyuan.feature.address;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.network.model.AddressRequest;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

public class AddressEditActivity extends BaseActivity<AddressEditContract.View, AddressEditPresenter>
        implements AddressEditContract.View {

    public static final String EXTRA_ADDRESS_ID = "extra_address_id";
    public static final String EXTRA_RECEIVER = "extra_receiver";
    public static final String EXTRA_PHONE = "extra_phone";
    public static final String EXTRA_PROVINCE = "extra_province";
    public static final String EXTRA_CITY = "extra_city";
    public static final String EXTRA_DETAIL = "extra_detail";
    public static final String EXTRA_IS_DEFAULT = "extra_is_default";

    private EditText receiverInput;
    private EditText phoneInput;
    private EditText provinceInput;
    private EditText cityInput;
    private EditText detailInput;
    private CheckBox defaultCheck;
    private Button saveButton;
    private TextView titleView;

    private Long addressId;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_address_edit;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        receiverInput = findViewById(R.id.input_receiver);
        phoneInput = findViewById(R.id.input_phone);
        provinceInput = findViewById(R.id.input_province);
        cityInput = findViewById(R.id.input_city);
        detailInput = findViewById(R.id.input_detail);
        defaultCheck = findViewById(R.id.input_default);
        saveButton = findViewById(R.id.button_save_address);
        titleView = findViewById(R.id.address_edit_title);

        addressId = getIntent().getLongExtra(EXTRA_ADDRESS_ID, 0L);
        if (addressId == 0L && titleView != null) {
            titleView.setText(getString(R.string.address_add));
        }
        receiverInput.setText(getIntent().getStringExtra(EXTRA_RECEIVER));
        phoneInput.setText(getIntent().getStringExtra(EXTRA_PHONE));
        provinceInput.setText(getIntent().getStringExtra(EXTRA_PROVINCE));
        cityInput.setText(getIntent().getStringExtra(EXTRA_CITY));
        detailInput.setText(getIntent().getStringExtra(EXTRA_DETAIL));
        defaultCheck.setChecked(getIntent().getBooleanExtra(EXTRA_IS_DEFAULT, false));

        saveButton.setOnClickListener(v -> saveAddress());
    }

    @Override
    protected void initData() {
    }

    @Override
    protected AddressEditPresenter createPresenter() {
        return new AddressEditPresenter();
    }

    @Override
    public void showSaving(boolean saving) {
        saveButton.setEnabled(!saving);
    }

    @Override
    public void onSaveSuccess() {
        ToastUtils.showToast(getString(R.string.address_save_success));
        finish();
    }

    @Override
    public void showError(String message) {
        if (message != null && !message.isEmpty()) {
            ToastUtils.showToast(message);
        }
    }

    private void saveAddress() {
        String receiver = receiverInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String province = provinceInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String detail = detailInput.getText().toString().trim();
        boolean isDefault = defaultCheck.isChecked();
        if (TextUtils.isEmpty(receiver) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(detail)) {
            ToastUtils.showToast(getString(R.string.address_fill_required));
            return;
        }
        AddressRequest request = new AddressRequest(receiver, phone, province, city, detail, isDefault);
        if (presenter != null) {
            presenter.saveAddress(this, addressId == 0 ? null : addressId, request);
        }
    }
}
