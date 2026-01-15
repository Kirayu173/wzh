package com.wzh.suyuan.feature.trace;

import android.net.Uri;
import android.text.TextUtils;

import com.wzh.suyuan.ui.mvp.base.BasePresenter;

public class TraceScanPresenter extends BasePresenter<TraceScanContract.View> {
    public void handleScanResult(String rawContent) {
        String traceCode = resolveTraceCode(rawContent);
        TraceScanContract.View view = getView();
        if (view == null) {
            return;
        }
        if (TextUtils.isEmpty(traceCode)) {
            view.showScanError("二维码内容无效，请手动输入");
            return;
        }
        view.openTraceDetail(traceCode);
    }

    public void handleManualInput(String input) {
        String traceCode = resolveTraceCode(input);
        TraceScanContract.View view = getView();
        if (view == null) {
            return;
        }
        if (TextUtils.isEmpty(traceCode)) {
            view.showScanError("请输入有效的溯源码");
            return;
        }
        view.openTraceDetail(traceCode);
    }

    private String resolveTraceCode(String content) {
        if (content == null) {
            return null;
        }
        String trimmed = content.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.startsWith("http")) {
            Uri uri = Uri.parse(trimmed);
            String traceCode = uri.getQueryParameter("traceCode");
            if (!TextUtils.isEmpty(traceCode)) {
                return traceCode.trim();
            }
            String lastSegment = uri.getLastPathSegment();
            if (!TextUtils.isEmpty(lastSegment)) {
                return lastSegment.trim();
            }
            return null;
        }
        int index = trimmed.indexOf("traceCode=");
        if (index >= 0) {
            String value = trimmed.substring(index + "traceCode=".length());
            int ampersand = value.indexOf('&');
            if (ampersand >= 0) {
                value = value.substring(0, ampersand);
            }
            return value.trim();
        }
        return trimmed;
    }
}
