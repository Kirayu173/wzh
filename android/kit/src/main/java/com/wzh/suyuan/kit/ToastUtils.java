package com.wzh.suyuan.kit;

import android.content.Context;
import android.widget.Toast;

public final class ToastUtils {
    private static Context appContext;

    private ToastUtils() {
    }

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static void showToast(String message) {
        if (appContext == null || message == null) {
            return;
        }
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
    }
}
