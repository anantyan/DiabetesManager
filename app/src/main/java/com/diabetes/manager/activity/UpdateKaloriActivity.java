package com.diabetes.manager.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.diabetes.manager.R;
import com.diabetes.manager.api.KaloriApi;
import com.diabetes.manager.api.MakananApi;
import com.diabetes.manager.component.RecordsComponent;
import com.diabetes.manager.component.ResponseComponent;
import com.diabetes.manager.component.SharedPreferenceComponent;
import com.diabetes.manager.util.RetrofitUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UpdateKaloriActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id._input_makanan) Spinner inputMakanan;
    @BindView(R.id._btn_save) Button btnSave;

    ProgressDialog progressDialog;

    private List<RecordsComponent> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_kalori);
        ButterKnife.bind(UpdateKaloriActivity.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadDataSpinner();
        btnSave();
    }

    private void btnSave() {

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String namaMakanan = String.valueOf(inputMakanan.getSelectedItem().toString().trim());

                progressDialog = new ProgressDialog(UpdateKaloriActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Tunggu...");
                progressDialog.show();

                Retrofit retrofit = RetrofitUtil.getClient();
                KaloriApi kaloriApi = retrofit.create(KaloriApi.class);
                Call<ResponseComponent> call = kaloriApi.updateKalori(new SharedPreferenceComponent(UpdateKaloriActivity.this).getDataId(), namaMakanan);
                call.enqueue(new Callback<ResponseComponent>() {

                    @Override
                    public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                        String error = response.body().getError();
                        String status = response.body().getStatus();
                        progressDialog.dismiss();
                        if(error.equals("1")) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdateKaloriActivity.this);
                            alertDialogBuilder.setTitle("Peringatan!");
                            alertDialogBuilder
                                    .setMessage(status)
                                    .setCancelable(false)
                                    .setPositiveButton("Iya",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        } else {
                            Toast.makeText(UpdateKaloriActivity.this, status, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseComponent> call, Throwable t) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(R.id._update_kalori), "Kesalahan pada jaringan!", Snackbar.LENGTH_LONG)
                                .setAction("Oke", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .setDuration(3000)
                                .show();
                    }
                });
            }
        });
    }

    private void loadDataSpinner() {

        progressDialog = new ProgressDialog(UpdateKaloriActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Tunggu...");
        progressDialog.show();

        Retrofit retrofit = RetrofitUtil.getClient();
        MakananApi makananApi = retrofit.create(MakananApi.class);
        Call<ResponseComponent> call = makananApi.selectMakanan();
        call.enqueue(new Callback<ResponseComponent>() {

            @Override
            public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                progressDialog.dismiss();
                records = response.body().getRecords();
                if(response.isSuccessful()){
                    /*select key & value spinner*/
                    List<String> listMakanan = new ArrayList<>();
                    for (int i = 0; i < records.size(); i++){
                        listMakanan.add(records.get(i).getNamaMakanan());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateKaloriActivity.this, android.R.layout.simple_spinner_item, listMakanan);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    inputMakanan.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseComponent> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(findViewById(R.id._update_kalori), "Kesalahan pada jaringan!", Snackbar.LENGTH_LONG)
                        .setAction("Oke", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setDuration(3000)
                        .show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
