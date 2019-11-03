package id.co.medical.management.api;

import id.co.medical.management.component.ResponseComponent;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApi {

    @FormUrlEncoded
    @POST("?p=Auth&x=register")
    Call<ResponseComponent> register(
            @Field("nama") String name,
            @Field("nomor_hp") String nomorHP,
            @Field("re_nomor_hp") String reNomorHP
    );

    @FormUrlEncoded
    @POST("?p=Auth&x=login")
    Call<ResponseComponent> login(
            @Field("nomor_hp") String nomorHP
    );

    @FormUrlEncoded
    @POST("?p=Auth&x=profile")
    Call<ResponseComponent> profile(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("?p=Auth&x=cekDataId")
    Call<ResponseComponent> cekDataId(
            @Field("id") String id
    );

    @FormUrlEncoded
    @POST("?p=Auth&x=profile_update_nama")
    Call<ResponseComponent> profileUpdateName(
            @Field("id") String id,
            @Field("nama") String name
    );

    @FormUrlEncoded
    @POST("?p=Auth&x=profile_update_nomor")
    Call<ResponseComponent> profileUpdateNomor(
            @Field("id") String id,
            @Field("nomor_hp") String nomorhp,
            @Field("pin") String pin
    );
}
