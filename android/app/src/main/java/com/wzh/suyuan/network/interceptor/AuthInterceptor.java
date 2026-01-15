package com.wzh.suyuan.network.interceptor;

import java.io.IOException;

import org.greenrobot.eventbus.EventBus;

import com.wzh.suyuan.App;
import com.wzh.suyuan.data.auth.AuthManager;
import com.wzh.suyuan.event.AuthEvent;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.code() == 401 && !isAuthRequest(request)) {
            AuthManager.clearSession(App.getInstance());
            EventBus.getDefault().post(new AuthEvent(AuthEvent.Type.UNAUTHORIZED));
        }
        return response;
    }

    private boolean isAuthRequest(Request request) {
        String path = request.url().encodedPath();
        return path.startsWith("/auth/login") || path.startsWith("/auth/register");
    }
}
