package com.wzh.suyuan.feature.cart;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.wzh.suyuan.AppExecutors;
import com.wzh.suyuan.data.auth.AuthManager;
import com.wzh.suyuan.data.cart.CartMapper;
import com.wzh.suyuan.data.db.AppDatabase;
import com.wzh.suyuan.data.db.entity.CartEntity;
import com.wzh.suyuan.kit.NetworkUtils;
import com.wzh.suyuan.network.ApiClient;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.CartItem;
import com.wzh.suyuan.network.model.CartSelectRequest;
import com.wzh.suyuan.network.model.CartUpdateRequest;
import com.wzh.suyuan.network.model.AuthUser;
import com.wzh.suyuan.ui.mvp.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartPresenter extends BasePresenter<CartContract.View> {
    private static final String TAG = "CartPresenter";

    public void loadCart(Context context) {
        if (context == null) {
            return;
        }
        if (!NetworkUtils.isNetworkAvailable(context)) {
            loadLocalCart(context, "网络不可用，显示缓存数据");
            return;
        }
        ApiClient.getService().getCartItems().enqueue(new Callback<BaseResponse<List<CartItem>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<CartItem>>> call,
                                   Response<BaseResponse<List<CartItem>>> response) {
                CartContract.View view = getView();
                if (view == null) {
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "cart list failed http=" + response.code());
                    loadLocalCart(context, "购物车加载失败");
                    return;
                }
                BaseResponse<List<CartItem>> body = response.body();
                if (!body.isSuccess()) {
                    loadLocalCart(context, body.getMessage());
                    return;
                }
                List<CartItem> items = body.getData();
                syncRemoteCart(context, items == null ? new ArrayList<>() : items);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<CartItem>>> call, Throwable t) {
                Log.w(TAG, "cart list error", t);
                loadLocalCart(context, "网络异常，显示缓存数据");
            }
        });
    }

    public void updateQuantity(Context context, CartEntity item, int newQuantity, int oldQuantity) {
        if (context == null || item == null) {
            return;
        }
        item.setUpdatedAt(System.currentTimeMillis());
        if (!item.isSynced() || !NetworkUtils.isNetworkAvailable(context)) {
            updateLocalItem(context, item);
            return;
        }
        ApiClient.getService().updateCartItem(item.getId(), new CartUpdateRequest(newQuantity))
                .enqueue(new Callback<BaseResponse<CartItem>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<CartItem>> call,
                                           Response<BaseResponse<CartItem>> response) {
                        CartContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "cart update failed http=" + response.code());
                            view.onCartItemUpdateFailed(item, oldQuantity, "数量更新失败");
                            return;
                        }
                        BaseResponse<CartItem> body = response.body();
                        if (!body.isSuccess()) {
                            view.onCartItemUpdateFailed(item, oldQuantity, body.getMessage());
                            return;
                        }
                        updateLocalItem(context, item);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<CartItem>> call, Throwable t) {
                        CartContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        Log.w(TAG, "cart update error", t);
                        view.onCartItemUpdateFailed(item, oldQuantity, "网络异常，请稍后重试");
                    }
                });
    }

    public void updateSelection(Context context, CartEntity item, boolean oldSelected) {
        if (context == null || item == null) {
            return;
        }
        item.setUpdatedAt(System.currentTimeMillis());
        if (!item.isSynced() || !NetworkUtils.isNetworkAvailable(context)) {
            updateLocalItem(context, item);
            return;
        }
        ApiClient.getService().updateCartSelection(item.getId(), new CartSelectRequest(item.isSelected()))
                .enqueue(new Callback<BaseResponse<CartItem>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<CartItem>> call,
                                           Response<BaseResponse<CartItem>> response) {
                        CartContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "cart select failed http=" + response.code());
                            view.onCartItemSelectionFailed(item, oldSelected, "选择状态更新失败");
                            return;
                        }
                        BaseResponse<CartItem> body = response.body();
                        if (!body.isSuccess()) {
                            view.onCartItemSelectionFailed(item, oldSelected, body.getMessage());
                            return;
                        }
                        updateLocalItem(context, item);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<CartItem>> call, Throwable t) {
                        CartContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        Log.w(TAG, "cart select error", t);
                        view.onCartItemSelectionFailed(item, oldSelected, "网络异常，请稍后重试");
                    }
                });
    }

    public void deleteItem(Context context, CartEntity item, int position) {
        if (context == null || item == null) {
            return;
        }
        if (!item.isSynced() || !NetworkUtils.isNetworkAvailable(context)) {
            deleteLocalItem(context, item);
            return;
        }
        ApiClient.getService().deleteCartItem(item.getId())
                .enqueue(new Callback<BaseResponse<Object>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Object>> call,
                                           Response<BaseResponse<Object>> response) {
                        CartContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.w(TAG, "cart delete failed http=" + response.code());
                            view.onCartItemDeleteFailed(item, position, "删除失败");
                            return;
                        }
                        BaseResponse<Object> body = response.body();
                        if (!body.isSuccess()) {
                            view.onCartItemDeleteFailed(item, position, body.getMessage());
                            return;
                        }
                        deleteLocalItem(context, item);
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                        CartContract.View view = getView();
                        if (view == null) {
                            return;
                        }
                        Log.w(TAG, "cart delete error", t);
                        view.onCartItemDeleteFailed(item, position, "网络异常，请稍后重试");
                    }
                });
    }

    private void syncRemoteCart(Context context, List<CartItem> items) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            long userId = getUserId(context);
            List<CartEntity> entities = new ArrayList<>();
            for (CartItem item : items) {
                CartEntity entity = CartMapper.toEntity(userId, item);
                if (entity != null) {
                    entities.add(entity);
                }
            }
            AppDatabase db = AppDatabase.getInstance(context);
            db.cartDao().deleteSyncedByUser(userId);
            if (!entities.isEmpty()) {
                db.cartDao().insertAll(entities);
            }
            List<CartEntity> merged = db.cartDao().getByUser(userId);
            AppExecutors.getInstance().mainThread().execute(() -> {
                CartContract.View view = getView();
                if (view != null) {
                    view.onCartLoaded(merged);
                }
            });
        });
    }

    private void loadLocalCart(Context context, String message) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            long userId = getUserId(context);
            List<CartEntity> items = AppDatabase.getInstance(context)
                    .cartDao()
                    .getByUser(userId);
            AppExecutors.getInstance().mainThread().execute(() -> {
                CartContract.View view = getView();
                if (view == null) {
                    return;
                }
                if (items == null || items.isEmpty()) {
                    view.onCartLoadFailed(message);
                } else {
                    view.onCartLoaded(items);
                    view.showError(message);
                }
            });
        });
    }

    private void updateLocalItem(Context context, CartEntity item) {
        AppExecutors.getInstance().diskIO().execute(() ->
                AppDatabase.getInstance(context).cartDao().update(item));
    }

    private void deleteLocalItem(Context context, CartEntity item) {
        AppExecutors.getInstance().diskIO().execute(() ->
                AppDatabase.getInstance(context).cartDao().deleteById(item.getId()));
    }

    private long getUserId(Context context) {
        AuthUser user = AuthManager.getUser(context);
        if (user != null && user.getId() != null) {
            return user.getId();
        }
        return 0L;
    }
}
