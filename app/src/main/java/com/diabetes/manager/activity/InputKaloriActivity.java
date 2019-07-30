package com.diabetes.manager.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import com.diabetes.manager.api.KaloriApi;
import com.diabetes.manager.R;
import com.diabetes.manager.component.ResponseComponent;
import com.diabetes.manager.component.SharedPreferenceComponent;
import com.diabetes.manager.util.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InputKaloriActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id._input_nama) EditText inputNama;
    @BindView(R.id._input_umur) EditText inputUmur;
    @BindView(R.id._input_tb) EditText inputTB;
    @BindView(R.id._input_bb) EditText inputBB;
    @BindView(R.id._input_jk) Spinner inputJK;
    @BindView(R.id._input_aktif) Spinner inputAktif;
    @BindView(R.id._input_hamil) Spinner inputHamil;
    @BindView(R.id._btn_save) Button btnSave;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_kalori);
        ButterKnife.bind(InputKaloriActivity.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*script untuk spinner jenis kelamin*/
        List<String> jenisKelamin = new ArrayList<>();
        jenisKelamin.add("Pilihan...");
        jenisKelamin.add("Perempuan");
        jenisKelamin.add("Laki - laki");
        ArrayAdapter<String> spinnerJenisKelamin = new ArrayAdapter<String>(
                InputKaloriActivity.this,
                android.R.layout.simple_spinner_item, jenisKelamin){
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
        spinnerJenisKelamin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputJK.setAdapter(spinnerJenisKelamin);

        /*script untuk spinner aktifitas*/
        List<String> aktifitas = new ArrayList<>();
        aktifitas.add("Pilihan...");
        aktifitas.add("Ringan");
        aktifitas.add("Sedang");
        aktifitas.add("Berat");
        ArrayAdapter<String> spinnerAktifitas = new ArrayAdapter<String>(
                InputKaloriActivity.this,
                android.R.layout.simple_spinner_item, aktifitas){
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
        spinnerAktifitas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputAktif.setAdapter(spinnerAktifitas);

        /*script untuk spinner aktifitas*/
        List<String> hamil = new ArrayList<>();
        hamil.add("Pilihan...");
        hamil.add("Trimester 1");
        hamil.add("Trimester 2");
        hamil.add("Trimester 3");
        hamil.add("Menyusui");
        ArrayAdapter<String> spinnerHamil = new ArrayAdapter<String>(
                InputKaloriActivity.this,
                android.R.layout.simple_spinner_item, hamil){
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
        spinnerHamil.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputHamil.setAdapter(spinnerHamil);

        btnSave();
    }

    private void btnSave() {

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*value for spinner*/
                String[] valJenisKelamin = new String[]{"", "Perempuan", "Laki - laki"};
                String[] valAktifitas = new String[]{"", "Ringan", "Sedang", "Berat"};
                String[] valHamil = new String[]{"", "Trimester 1", "Trimester 2", "Trimester 3", "Menyusui"};

                String nama = inputNama.getText().toString().trim();
                String umur = inputUmur.getText().toString().trim();
                String tb = inputTB.getText().toString().trim();
                String bb = inputBB.getText().toString().trim();
                String jk = valJenisKelamin[inputJK.getSelectedItemPosition()];
                String aktif = valAktifitas[inputAktif.getSelectedItemPosition()];
                String hamil = valHamil[inputHamil.getSelectedItemPosition()];

                if(nama.equals("")){
                    Toast.makeText(InputKaloriActivity.this,
                            "Nama harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputNama.setError("Nama harus diisi!");
                    inputNama.requestFocus();
                }else if(jk.equals("")){
                    Toast.makeText(InputKaloriActivity.this,
                            "Jenis Kelamin harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputJK.requestFocus();
                }else if(umur.equals("")){
                    Toast.makeText(InputKaloriActivity.this,
                            "Umur harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputUmur.setError("Umur harus diisi!");
                    inputUmur.requestFocus();
                }else if(tb.equals("")){
                    Toast.makeText(InputKaloriActivity.this,
                            "Tinggi Badan harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputTB.setError("Tinggi Badan harus diisi!");
                    inputTB.requestFocus();
                }else if(bb.equals("")){
                    Toast.makeText(InputKaloriActivity.this,
                            "Berat Badan harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputBB.setError("Berat Badan harus diisi!");
                    inputBB.requestFocus();
                }else if(aktif.equals("")){
                    Toast.makeText(InputKaloriActivity.this,
                            "Aktifitas harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputAktif.requestFocus();
                }else{
                    progressDialog = new ProgressDialog(InputKaloriActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Tunggu...");
                    progressDialog.show();

                    Retrofit retrofit = RetrofitUtil.getClient();
                    KaloriApi kaloriApi = retrofit.create(KaloriApi.class);
                    Call<ResponseComponent> call = kaloriApi.insertKalori(new SharedPreferenceComponent(InputKaloriActivity.this).getDataId(), nama, jk, umur, tb, bb, aktif, hamil);
                    call.enqueue(new Callback<ResponseComponent>() {

                        @Override
                        public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                            String error = response.body().getError();
                            String status = response.body().getStatus();
                            progressDialog.dismiss();
                            if(error.equals("1")) {
                                Toast.makeText(InputKaloriActivity.this, status, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(InputKaloriActivity.this, status, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseComponent> call, Throwable t) {
                            progressDialog.dismiss();
                            Snackbar.make(findViewById(R.id._input_kalori), "Kesalahan pada jaringan!", Snackbar.LENGTH_LONG)
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
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
