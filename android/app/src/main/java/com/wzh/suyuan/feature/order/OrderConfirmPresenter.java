package com.wzh.suyuan.feature.order;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.wzh.suyuan.AppExecutors;
import com.wzh.suyuan.R;
import com.wzh.suyuan.data.db.AppDatabase;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.Address;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.CartItem;
import com.wzh.suyuan.network.model.OrderCreateRequest;
import com.wzh.suyuan.network.model.OrderCreateResponse;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderConfirmPresenter extends BasePresenter<OrderConfirmContract.View> {
    private static final String TAG = "OrderConfirmPresenter";

    public void loadAddresses(Context context) {
        if (context == null) {
            return;
        }
        ApiClient.getService().getAddresses().enqueue(new Callback<BaseResponse<List<Address>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Address>>> call,
                                   Response<BaseResponse<List<Address>>> response) {
                OrderConfirmContract.View view = getView();
                if (view == null) {
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "address load failed http=" + response.code());
                    view.showError("地址加载失败");
                    return;
                }
                BaseResponse<List<Address>> body = response.body();
                if (!body.isSuccess()) {
                    view.showError(body.getMessage());
                    return;
                }
                List<Address> addresses = body.getData();
                Address selected = pickDefault(addresses);
                view.showAddress(selected);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Address>>> call, Throwable t) {
                OrderConfirmContract.View view = getView();
                if (view != null) {
                    Log.w(TAG, "address load error", t);
                    view.showError("网络异常，请稍后重试");
                }
            }
        });
    }

    public void loadCartItems(Context context) {
        if (context == null) {
            return;
        }
        OrderConfirmContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        ApiClient.getService().getCartItems().enqueue(new Callback<BaseResponse<List<CartItem>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<CartItem>>> call,
                                   Response<BaseResponse<List<CartItem>>> response) {
                OrderConfirmContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "cart load failed http=" + response.code());
                    view.showError("购物车加载失败");
                    return;
                }
                BaseResponse<List<CartItem>> body = response.body();
                if (!body.isSuccess()) {
                    view.showError(body.getMessage());
                    return;
                }
                List<CartItem> data = filterSelected(body.getData());
                if (data.isEmpty()) {
                    view.showEmpty("请选择商品后再提交订单");
                } else {
                    view.showCartItems(data);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<CartItem>>> call, Throwable t) {
                OrderConfirmContract.View view = getView();
                if (view == null) {
                    return;
                }
                Log.w(TAG, "cart load error", t);
                view.showLoading(false);
                view.showError("网络异常，请稍后重试");
            }
        });
    }

    public void createOrder(Context context, Long addressId, String memo, String requestId, List<CartItem> items) {
        if (context == null || addressId == null || addressId <= 0 || items == null || items.isEmpty()) {
            return;
        }
        List<OrderCreateRequest.Item> requestItems = new ArrayList<>();
        for (CartItem item : items) {
            if (item.getId() == null || item.getProductId() == null) {
                continue;
            }
            int quantity = item.getQuantity() == null ? 1 : item.getQuantity();
            requestItems.add(new OrderCreateRequest.Item(item.getId(), item.getProductId(), quantity));
        }
        if (requestItems.isEmpty()) {
            OrderConfirmContract.View view = getView();
            if (view != null) {
                view.showError(context.getString(R.string.order_confirm_empty));
            }
            return;
        }
        OrderCreateRequest request = new OrderCreateRequest(addressId, memo, requestId, requestItems);
        OrderConfirmContract.View view = getView();
        if (view != null) {
            view.showLoading(true);
        }
        ApiClient.getService().createOrder(request).enqueue(new Callback<BaseResponse<OrderCreateResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<OrderCreateResponse>> call,
                                   Response<BaseResponse<OrderCreateResponse>> response) {
                OrderConfirmContract.View view = getView();
                if (view == null) {
                    return;
                }
                view.showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "order create failed http=" + response.code());
                    view.showError("下单失败，请重试");
                    return;
                }
                BaseResponse<OrderCreateResponse> body = response.body();
                if (!body.isSuccess() || body.getData() == null) {
                    view.showError(body.getMessage());
                    return;
                }
                clearLocalCart(context, items);
                view.onOrderCreated(body.getData());
            }

            @Override
            public void onFailure(Call<BaseResponse<OrderCreateResponse>> call, Throwable t) {
                OrderConfirmContract.View view = getView();
                if (view != null) {
                    Log.w(TAG, "order create error", t);
                    view.showLoading(false);
                    view.showError("网络异常，请稍后重试");
                }
            }
        });
    }

    private void clearLocalCart(Context context, List<CartItem> items) {
        if (context == null || items == null || items.isEmpty()) {
            return;
        }
        AppExecutors.getInstance().diskIO().execute(() -> {
            for (CartItem item : items) {
                if (item == null || item.getId() == null || item.getId() <= 0) {
                    continue;
                }
                AppDatabase.getInstance(context).cartDao().deleteById(item.getId());
            }
        });
    }

    private List<CartItem> filterSelected(List<CartItem> items) {
        List<CartItem> result = new ArrayList<>();
        if (items == null) {
            return result;
        }
        for (CartItem item : items) {
            if (Boolean.TRUE.equals(item.getSelected())) {
                result.add(item);
            }
        }
        return result;
    }

    private Address pickDefault(List<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }
        for (Address address : addresses) {
            if (Boolean.TRUE.equals(address.getIsDefault())) {
                return address;
            }
        }
        return addresses.get(0);
    }
}
