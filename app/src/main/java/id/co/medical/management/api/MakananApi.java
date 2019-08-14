package id.co.medical.management.api;


import id.co.medical.management.component.ResponseComponent;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MakananApi {

    @GET("Makanan.php?x=selectMakanan")
    Call<ResponseComponent> selectMakanan();

    @FormUrlEncoded
    @POST("Makanan.php?x=selectLaporan")
    Call<ResponseComponent> selectLaporan(
            @Field("id") String id
    );
}
