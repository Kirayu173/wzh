package com.wzh.suyuan.feature.admin.trace;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.wzh.suyuan.R;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTraceQrCodePresenter extends BasePresenter<AdminTraceQrCodeContract.View> {

    public void loadQrCode(String traceCode, android.content.Context context) {
        if (TextUtils.isEmpty(traceCode) || context == null) {
            AdminTraceQrCodeContract.View view = getView();
            if (view != null) {
                view.showError(context == null ? null : context.getString(R.string.admin_trace_qrcode_error));
            }
            return;
        }
        ApiClient.getService().getAdminTraceQrCode(traceCode)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        AdminTraceQrCodeContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            view.showError(context.getString(R.string.admin_trace_qrcode_error));
                            return;
                        }
                        try {
                            byte[] bytes = response.body().bytes();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            if (bitmap == null) {
                                view.showError(context.getString(R.string.admin_trace_qrcode_error));
                                return;
                            }
                            view.showQrCode(bitmap);
                        } catch (IOException ex) {
                            view.showError(context.getString(R.string.admin_trace_qrcode_error));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AdminTraceQrCodeContract.View view = getView();
                        if (view != null) {
                            view.showError(context.getString(R.string.error_network));
                        }
                    }
                });
    }
}
