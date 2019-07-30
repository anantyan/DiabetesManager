package com.diabetes.manager.api;

import com.diabetes.manager.component.ResponseComponent;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApi {

    @FormUrlEncoded
    @POST("Auth.php?x=register")
    Call<ResponseComponent> register(
            @Field("nomor_hp") String nomorHP,
            @Field("re_nomor_hp") String reNomorHP
    );

    @FormUrlEncoded
    @POST("Auth.php?x=login")
    Call<ResponseComponent> login(
            @Field("nomor_hp") String nomorHP
    );

    @GET("Auth.php?x=getData")
    Call<ResponseComponent> getData();
}
