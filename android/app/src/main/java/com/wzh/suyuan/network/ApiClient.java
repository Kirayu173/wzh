package com.wzh.suyuan.network;

import com.wzh.suyuan.network.interceptor.AuthInterceptor;
import com.wzh.suyuan.network.interceptor.HeaderInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public final class ApiClient {
    private static volatile Retrofit retrofit;

    private ApiClient() {
    }

    public static ApiService getService() {
        return getRetrofit().create(ApiService.class);
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (ApiClient.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(NetworkConfig.BASE_URL)
                            .client(buildClient())
                            .addConverterFactory(new FastjsonConverterFactory())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    private static OkHttpClient buildClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new AuthInterceptor())
                .addInterceptor(loggingInterceptor)
                .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }
}
