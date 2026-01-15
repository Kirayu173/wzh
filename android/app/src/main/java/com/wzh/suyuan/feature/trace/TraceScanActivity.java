package com.wzh.suyuan.feature.trace;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.wzh.suyuan.R;
import com.wzh.suyuan.kit.ToastUtils;
import com.wzh.suyuan.ui.activity.base.BaseActivity;

public class TraceScanActivity extends BaseActivity<TraceScanContract.View, TraceScanPresenter>
        implements TraceScanContract.View {

    private static final int REQUEST_CAMERA_PERMISSION = 2001;

    private EditText traceInput;
    private Button scanButton;
    private Button queryButton;
    private Button recordButton;

    private final ActivityResultLauncher<ScanOptions> scanLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result == null || TextUtils.isEmpty(result.getContents())) {
                    showScanError("扫描失败，请手动输入");
                    return;
                }
                if (presenter != null) {
                    presenter.handleScanResult(result.getContents());
                }
            });

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_trace_scan;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        traceInput = findViewById(R.id.trace_input);
        scanButton = findViewById(R.id.trace_scan_button);
        queryButton = findViewById(R.id.trace_query_button);
        recordButton = findViewById(R.id.trace_record_button);

        scanButton.setOnClickListener(v -> startScan());
        queryButton.setOnClickListener(v -> {
            if (presenter != null) {
                presenter.handleManualInput(traceInput.getText().toString());
            }
        });
        recordButton.setOnClickListener(v ->
                startActivity(new Intent(TraceScanActivity.this, TraceRecordActivity.class)));
    }

    @Override
    protected void initData() {
    }

    @Override
    protected TraceScanPresenter createPresenter() {
        return new TraceScanPresenter();
    }

    @Override
    public void showScanError(String message) {
        ToastUtils.showToast(message);
    }

    @Override
    public void openTraceDetail(String traceCode) {
        Intent intent = new Intent(this, TraceDetailActivity.class);
        intent.putExtra(TraceDetailActivity.EXTRA_TRACE_CODE, traceCode);
        startActivity(intent);
    }

    private void startScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt(getString(R.string.trace_scan_prompt));
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        scanLauncher.launch(options);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScan();
            } else {
                showScanError("需要相机权限才能扫码");
            }
        }
    }
}
