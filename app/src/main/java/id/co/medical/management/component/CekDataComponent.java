package id.co.medical.management.component;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import android.view.View;

import id.co.medical.management.activity.LoginActivity;
import id.co.medical.management.api.AuthApi;
import id.co.medical.management.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CekDataComponent {

    Context context;
    View view;

    public CekDataComponent(Context context, View view){
        this.context = context;
        this.view = view;
    }

    public void CekDataId(String id){

        Retrofit retrofit = RetrofitUtil.getClient();
        AuthApi authApi = retrofit.create(AuthApi.class);
        Call<ResponseComponent> call = authApi.cekDataId(id);
        call.enqueue(new Callback<ResponseComponent>() {

            @Override
            public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                assert response.body() != null;
                String error = response.body().getError();
                String status = response.body().getStatus();
                if(response.isSuccessful()){
                    if(error.equals("1")) {

                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("Konfirmasi");
                        alertDialogBuilder
                                .setMessage(status)
                                .setCancelable(false)
                                .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        SharedPreferencesComponent.setLoggedIn(context, false);
                                        new SharedPreferencesComponent(context).setDataOut();
                                        Intent i = new Intent(context, LoginActivity.class);
                                        context.startActivity(i);
                                        ((Activity)context).finish();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseComponent> call, Throwable t) {

            }
        });
    }
}
