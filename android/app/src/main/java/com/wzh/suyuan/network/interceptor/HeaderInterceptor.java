package com.wzh.suyuan.network.interceptor;

import com.wzh.suyuan.App;
import com.wzh.suyuan.BuildConfig;
import com.wzh.suyuan.data.auth.AuthManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder()
                .addHeader("X-App-Version", BuildConfig.VERSION_NAME)
                .addHeader("X-App-Platform", "android");
        String token = AuthManager.getToken(App.getInstance());
        if (token != null && !token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }
        Request request = builder.build();
        return chain.proceed(request);
    }
}
