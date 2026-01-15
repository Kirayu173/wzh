package com.wzh.suyuan;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.wzh.suyuan.data.db.AppDatabase;
import com.wzh.suyuan.kit.NetworkUtils;
import com.wzh.suyuan.kit.ToastUtils;

public class App extends Application {
    private static final String TAG = "SuyuanApp";
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ToastUtils.init(this);
        AppDatabase.getInstance(this);
        Log.i(TAG, "appVersion=" + BuildConfig.VERSION_NAME
                + ", buildType=" + BuildConfig.BUILD_TYPE
                + ", deviceApi=" + Build.VERSION.SDK_INT
                + ", network=" + NetworkUtils.getNetworkTypeName(this));
    }
}
