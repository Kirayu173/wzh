package com.wzh.suyuan.feature.order;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.OrderPage;
import com.wzh.suyuan.network.model.OrderSummary;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListPresenter extends BasePresenter<OrderListContract.View> {
    private static final String TAG = "OrderListPresenter";
    private static final String CACHE_ALL = "ALL";

    private final Map<String, List<OrderSummary>> cachedOrders = new HashMap<>();

    public void loadOrders(Context context, String status, int page, int size) {
        if (context == null) {
            return;
        }
        OrderListContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        String statusKey = status == null || status.isEmpty() ? CACHE_ALL : status;
        String cacheKey = statusKey + "_" + page;
        ApiClient.getService().getOrders(status, page, size).enqueue(new Callback<BaseResponse<OrderPage>>() {
            @Override
            public void onResponse(Call<BaseResponse<OrderPage>> call,
                                   Response<BaseResponse<OrderPage>> response) {
                OrderListContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "order list failed http=" + response.code());
                    showCacheOrError(view, cacheKey, "订单加载失败");
                    return;
                }
                BaseResponse<OrderPage> body = response.body();
                if (!body.isSuccess() || body.getData() == null) {
                    showCacheOrError(view, cacheKey, body.getMessage());
                    return;
                }
                OrderPage pageData = body.getData();
                List<OrderSummary> items = pageData == null ? null : pageData.getItems();
                if (items == null) {
                    items = new ArrayList<>();
                }
                if (items.isEmpty() && page == 1) {
                    view.showEmpty("暂无订单");
                    return;
                }
                cachedOrders.put(cacheKey, items);
                long total = pageData == null ? 0 : pageData.getTotal();
                view.showOrders(items, page, size, total);
            }

            @Override
            public void onFailure(Call<BaseResponse<OrderPage>> call, Throwable t) {
                OrderListContract.View view = getView();
                if (view == null) {
                    return;
                }
                Log.w(TAG, "order list error", t);
                view.showLoading(false);
                showCacheOrError(view, cacheKey, "网络异常，请稍后重试");
            }
        });
    }

    private void showCacheOrError(OrderListContract.View view, String cacheKey, String message) {
        List<OrderSummary> cached = cachedOrders.get(cacheKey);
        if (cached != null && !cached.isEmpty()) {
            view.showCachedOrders(cached);
            view.showError(message);
        } else {
            view.showEmpty(message == null ? "订单加载失败" : message);
        }
    }
}
