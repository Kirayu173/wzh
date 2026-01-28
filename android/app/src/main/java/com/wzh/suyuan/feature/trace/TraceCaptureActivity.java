package com.wzh.suyuan.feature.trace;

import android.os.Bundle;
import android.view.View;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.wzh.suyuan.R;

public class TraceCaptureActivity extends CaptureActivity {

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_trace_capture);
        return findViewById(R.id.zxing_barcode_scanner);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View closeButton = findViewById(R.id.trace_capture_close);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> finish());
        }
    }
}
