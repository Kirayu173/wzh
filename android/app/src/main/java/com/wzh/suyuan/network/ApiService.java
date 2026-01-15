package com.wzh.suyuan.network;

import java.util.List;

import com.wzh.suyuan.network.model.Address;
import com.wzh.suyuan.network.model.AddressRequest;
import com.wzh.suyuan.network.model.AuthUser;
import com.wzh.suyuan.network.model.BaseResponse;
import com.wzh.suyuan.network.model.CartAddRequest;
import com.wzh.suyuan.network.model.CartAddResponse;
import com.wzh.suyuan.network.model.CartItem;
import com.wzh.suyuan.network.model.CartSelectRequest;
import com.wzh.suyuan.network.model.CartUpdateRequest;
import com.wzh.suyuan.network.model.LoginRequest;
import com.wzh.suyuan.network.model.LoginResponse;
import com.wzh.suyuan.network.model.OrderCreateRequest;
import com.wzh.suyuan.network.model.OrderCreateResponse;
import com.wzh.suyuan.network.model.OrderDetail;
import com.wzh.suyuan.network.model.OrderPage;
import com.wzh.suyuan.network.model.Product;
import com.wzh.suyuan.network.model.ProductPage;
import com.wzh.suyuan.network.model.RegisterRequest;
import com.wzh.suyuan.network.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("health")
    Call<BaseResponse<Object>> health();

    @POST("auth/register")
    Call<BaseResponse<RegisterResponse>> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<BaseResponse<LoginResponse>> login(@Body LoginRequest request);

    @GET("auth/me")
    Call<BaseResponse<AuthUser>> me();

    @GET("products")
    Call<BaseResponse<ProductPage>> getProducts(@Query("page") int page, @Query("size") int size);

    @GET("products/{id}")
    Call<BaseResponse<Product>> getProductDetail(@Path("id") long id);

    @GET("cart")
    Call<BaseResponse<List<CartItem>>> getCartItems();

    @POST("cart")
    Call<BaseResponse<CartAddResponse>> addToCart(@Body CartAddRequest request);

    @PUT("cart/{id}")
    Call<BaseResponse<CartItem>> updateCartItem(@Path("id") long id, @Body CartUpdateRequest request);

    @PATCH("cart/{id}/select")
    Call<BaseResponse<CartItem>> updateCartSelection(@Path("id") long id, @Body CartSelectRequest request);

    @DELETE("cart/{id}")
    Call<BaseResponse<Object>> deleteCartItem(@Path("id") long id);

    @GET("addresses")
    Call<BaseResponse<List<Address>>> getAddresses();

    @POST("addresses")
    Call<BaseResponse<Address>> createAddress(@Body AddressRequest request);

    @PUT("addresses/{id}")
    Call<BaseResponse<Address>> updateAddress(@Path("id") long id, @Body AddressRequest request);

    @DELETE("addresses/{id}")
    Call<BaseResponse<Object>> deleteAddress(@Path("id") long id);

    @PATCH("addresses/{id}/default")
    Call<BaseResponse<Address>> setDefaultAddress(@Path("id") long id);

    @POST("orders")
    Call<BaseResponse<OrderCreateResponse>> createOrder(@Body OrderCreateRequest request);

    @GET("orders")
    Call<BaseResponse<OrderPage>> getOrders(@Query("status") String status,
                                            @Query("page") int page,
                                            @Query("size") int size);

    @GET("orders/{id}")
    Call<BaseResponse<OrderDetail>> getOrderDetail(@Path("id") long id);

    @POST("orders/{id}/pay")
    Call<BaseResponse<OrderDetail>> payOrder(@Path("id") long id);

    @POST("orders/{id}/cancel")
    Call<BaseResponse<OrderDetail>> cancelOrder(@Path("id") long id);

    @POST("orders/{id}/confirm")
    Call<BaseResponse<OrderDetail>> confirmOrder(@Path("id") long id);
}
