package com.diabetes.manager.api;

import com.diabetes.manager.component.ResponseComponent;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface KaloriApi {

    @FormUrlEncoded
    @POST("Kalori.php?x=selectKalori")
    Call<ResponseComponent> selectKalori(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("Kalori.php?x=insertKalori")
    Call<ResponseComponent> insertKalori(
            @Field("id") String id,
            @Field("nama") String nama,
            @Field("jk") String jk,
            @Field("umur") String umur,
            @Field("tb") String tb,
            @Field("bb") String bb,
            @Field("aktif") String aktif,
            @Field("hamil") String hamil
    );

    @FormUrlEncoded
    @POST("Kalori.php?x=updateKalori")
    Call<ResponseComponent> updateKalori(
            @Field("id") String id,
            @Field("nama_makanan") String namaMakanan,
            @Field("jenis_olahraga") String jenisOlahraga,
            @Field("durasi_olahraga") String durasiOlahraga,
            @Field("cek_gula_darah") String gulaDarah,
            @Field("hasil_cek_gula_darah") String hasilGulaDarah
    );
}
