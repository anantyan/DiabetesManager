package com.diabetes.manager.api;

import com.diabetes.manager.component.ResponseComponent;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MakananApi {

    @GET("Makanan.php?x=selectMakanan")
    Call<ResponseComponent> selectMakanan();
}
