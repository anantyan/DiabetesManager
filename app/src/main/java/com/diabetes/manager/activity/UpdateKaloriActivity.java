package com.diabetes.manager.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
    @BindView(R.id._btn_next) Button btnNext;

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
        btnNext();
    }

    private void btnNext() {

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namaMakanan = String.valueOf(inputMakanan.getSelectedItem().toString().trim());

                if(namaMakanan.equals("Pilihan...")){
                    Toast.makeText(UpdateKaloriActivity.this,
                            "Nama Makanan harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputMakanan.requestFocus();
                }else{
                    Intent intent = new Intent(UpdateKaloriActivity.this, NextUpdateKaloriActivity.class);
                    intent.putExtra("getNamaMakanan", namaMakanan);
                    startActivity(intent);
                }
            }
        });
    }

    private void loadDataSpinner() {
        final List<String> listMakanan = new ArrayList<>();
        listMakanan.add(0, "Pilihan...");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateKaloriActivity.this, android.R.layout.simple_spinner_item, listMakanan){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputMakanan.setAdapter(adapter);

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
                    for (int i = 0; i < records.size(); i++){
                        listMakanan.add(records.get(i).getNamaMakanan());
                    }
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
