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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diabetes.manager.R;
import com.diabetes.manager.api.KaloriApi;
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

public class NextUpdateKaloriActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id._btn_save) Button btnSave;
    @BindView(R.id._input_olahraga) CheckBox inputOlahraga;
    @BindView(R.id._input_jenis_olahraga) EditText inputJenisOlahraga;
    @BindView(R.id._input_durasi_olahraga) Spinner inputDurasiOlahraga;
    @BindView(R.id._input_cek_gula) CheckBox inputCekGulaDarah;
    @BindView(R.id._input_cek_gula_darah) EditText inputGulaDarah;
    @BindView(R.id._input_hasil_cek_gula_darah) Spinner inputHasilGulaDarah;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_update_kalori);
        ButterKnife.bind(NextUpdateKaloriActivity.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spinnerDurasiOlahraga();
        spinnerHasilGulaDarah();
        inputOlahraga();
        inputCekGulaDarah();
        btnSave();
    }

    private void spinnerHasilGulaDarah() {
        final List<String> listHasilGulaDarah = new ArrayList<>();
        listHasilGulaDarah.add("Pilihan...");
        listHasilGulaDarah.add("Gula Darah Puasa");
        listHasilGulaDarah.add("Gula Darah Sewaktu");
        listHasilGulaDarah.add("Gula Darah 2 Jam setelah makan");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NextUpdateKaloriActivity.this, android.R.layout.simple_spinner_item, listHasilGulaDarah){
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
        inputHasilGulaDarah.setAdapter(adapter);
    }

    private void inputCekGulaDarah() {
        inputGulaDarah.setEnabled(false);
        inputHasilGulaDarah.setEnabled(false);

        inputCekGulaDarah.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    inputGulaDarah.setEnabled(false);
                    inputHasilGulaDarah.setEnabled(false);
                    btnSave();

                    /*set reset*/
                    inputJenisOlahraga.setText("");
                    inputDurasiOlahraga.setSelection(0);
                    inputGulaDarah.setText("");
                    inputHasilGulaDarah.setSelection(0);
                }else{
                    inputGulaDarah.setEnabled(true);
                    inputHasilGulaDarah.setEnabled(true);
                    btnSaveCekGulaDarah();
                }
            }
        });
    }

    private void inputOlahraga() {
        inputJenisOlahraga.setEnabled(false);
        inputDurasiOlahraga.setEnabled(false);

        inputOlahraga.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    inputJenisOlahraga.setEnabled(false);
                    inputDurasiOlahraga.setEnabled(false);
                    btnSave();

                    /*set reset*/
                    inputJenisOlahraga.setText("");
                    inputDurasiOlahraga.setSelection(0);
                    inputGulaDarah.setText("");
                    inputHasilGulaDarah.setSelection(0);
                }else{
                    inputJenisOlahraga.setEnabled(true);
                    inputDurasiOlahraga.setEnabled(true);
                    btnSaveOlahraga();
                }
            }
        });
    }

    private void spinnerDurasiOlahraga() {
        final List<String> listDurasiOlahraga = new ArrayList<>();
        listDurasiOlahraga.add("Pilihan...");
        listDurasiOlahraga.add("10 Menit");
        listDurasiOlahraga.add("15 Menit");
        listDurasiOlahraga.add("30 Menit");
        listDurasiOlahraga.add("1 Jam");
        listDurasiOlahraga.add("Lebih dari 1 Jam");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NextUpdateKaloriActivity.this, android.R.layout.simple_spinner_item, listDurasiOlahraga){
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
        inputDurasiOlahraga.setAdapter(adapter);
    }

    private void btnSaveCekGulaDarah() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] valDurasiOlahraga = new String[]{"", "10 Menit", "15 Menit", "30 Menit", "1 Jam", "Lebih dari 1 Jam"};
                String[] valHasilGulaDarah = new String[]{"", "Gula Darah Puasa", "Gula Darah Sewaktu", "Gula Darah 2 Jam setelah makan"};

                Intent intent = getIntent();
                String id = new SharedPreferenceComponent(NextUpdateKaloriActivity.this).getDataId();
                String namaMakanan = intent.getStringExtra("getNamaMakanan");
                String jenisOlahraga = inputJenisOlahraga.getText().toString().trim();
                String durasiOlahraga = valDurasiOlahraga[inputDurasiOlahraga.getSelectedItemPosition()];
                String gulaDarah = inputGulaDarah.getText().toString().trim();
                String hasilGulaDarah = valHasilGulaDarah[inputHasilGulaDarah.getSelectedItemPosition()];

                if(gulaDarah.equals("")){
                    Toast.makeText(NextUpdateKaloriActivity.this,
                            "Cek Gula Darah harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputGulaDarah.setError("Cek Gula Darah harus diisi!");
                    inputGulaDarah.requestFocus();
                }else if(hasilGulaDarah.equals("")){
                    Toast.makeText(NextUpdateKaloriActivity.this,
                            "Hasil Cek Gula Darah harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputHasilGulaDarah.requestFocus();
                }else{
                    Toast.makeText(NextUpdateKaloriActivity.this, "id:"+id+", namaMakanan:"+namaMakanan+", jenisOlahraga:"+jenisOlahraga+", durasiOlahraga:"+durasiOlahraga+", cekGulaDarah:"+gulaDarah+", hasilGulaDarah:"+hasilGulaDarah, Toast.LENGTH_SHORT).show();
                    /*progressDialog = new ProgressDialog(NextUpdateKaloriActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Tunggu...");
                    progressDialog.show();

                    Retrofit retrofit = RetrofitUtil.getClient();
                    KaloriApi kaloriApi = retrofit.create(KaloriApi.class);
                    Call<ResponseComponent> call = kaloriApi.updateKalori(id, namaMakanan, jenisOlahraga, durasiOlahraga);
                    call.enqueue(new Callback<ResponseComponent>() {
                        @Override
                        public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                            String error = response.body().getError();
                            String status = response.body().getStatus();
                            progressDialog.dismiss();
                            if(error.equals("1")) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NextUpdateKaloriActivity.this);
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
                                Toast.makeText(NextUpdateKaloriActivity.this, status, Toast.LENGTH_SHORT).show();
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
                    });*/
                }
            }
        });
    }

    private void btnSaveOlahraga() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] valDurasiOlahraga = new String[]{"", "10 Menit", "15 Menit", "30 Menit", "1 Jam", "Lebih dari 1 Jam"};
                String[] valHasilGulaDarah = new String[]{"", "Gula Darah Puasa", "Gula Darah Sewaktu", "Gula Darah 2 Jam setelah makan"};

                Intent intent = getIntent();
                String id = new SharedPreferenceComponent(NextUpdateKaloriActivity.this).getDataId();
                String namaMakanan = intent.getStringExtra("getNamaMakanan");
                String jenisOlahraga = inputJenisOlahraga.getText().toString().trim();
                String durasiOlahraga = valDurasiOlahraga[inputDurasiOlahraga.getSelectedItemPosition()];
                String gulaDarah = inputGulaDarah.getText().toString().trim();
                String hasilGulaDarah = valHasilGulaDarah[inputHasilGulaDarah.getSelectedItemPosition()];

                if(jenisOlahraga.equals("")){
                    Toast.makeText(NextUpdateKaloriActivity.this,
                            "Jenis Olahraga harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputJenisOlahraga.setError("Jenis Olahraga Darah harus diisi!");
                    inputJenisOlahraga.requestFocus();
                }else if(durasiOlahraga.equals("")){
                    Toast.makeText(NextUpdateKaloriActivity.this,
                            "Durasi Olahraga harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputDurasiOlahraga.requestFocus();
                }else {
                    Toast.makeText(NextUpdateKaloriActivity.this, "id:" + id + ", namaMakanan:" + namaMakanan + ", jenisOlahraga:" + jenisOlahraga + ", durasiOlahraga:" + durasiOlahraga + ", cekGulaDarah:" + gulaDarah + ", hasilGulaDarah:" + hasilGulaDarah, Toast.LENGTH_SHORT).show();
                    /*progressDialog = new ProgressDialog(NextUpdateKaloriActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Tunggu...");
                    progressDialog.show();

                    Retrofit retrofit = RetrofitUtil.getClient();
                    KaloriApi kaloriApi = retrofit.create(KaloriApi.class);
                    Call<ResponseComponent> call = kaloriApi.updateKalori(id, namaMakanan, jenisOlahraga, durasiOlahraga);
                    call.enqueue(new Callback<ResponseComponent>() {
                        @Override
                        public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                            String error = response.body().getError();
                            String status = response.body().getStatus();
                            progressDialog.dismiss();
                            if(error.equals("1")) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NextUpdateKaloriActivity.this);
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
                                Toast.makeText(NextUpdateKaloriActivity.this, status, Toast.LENGTH_SHORT).show();
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
                    });*/
                }
            }
        });
    }

    private void btnSave() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] valDurasiOlahraga = new String[]{"", "10 Menit", "15 Menit", "30 Menit", "1 Jam", "Lebih dari 1 Jam"};
                String[] valHasilGulaDarah = new String[]{"", "Gula Darah Puasa", "Gula Darah Sewaktu", "Gula Darah 2 Jam setelah makan"};

                Intent intent = getIntent();
                String id = new SharedPreferenceComponent(NextUpdateKaloriActivity.this).getDataId();
                String namaMakanan = intent.getStringExtra("getNamaMakanan");
                String jenisOlahraga = inputJenisOlahraga.getText().toString().trim();
                String durasiOlahraga = valDurasiOlahraga[inputDurasiOlahraga.getSelectedItemPosition()];
                String gulaDarah = inputGulaDarah.getText().toString().trim();
                String hasilGulaDarah = valHasilGulaDarah[inputHasilGulaDarah.getSelectedItemPosition()];

                Toast.makeText(NextUpdateKaloriActivity.this, "id:"+id+", namaMakanan:"+namaMakanan+", jenisOlahraga:"+jenisOlahraga+", durasiOlahraga:"+durasiOlahraga+", cekGulaDarah:"+gulaDarah+", hasilGulaDarah:"+hasilGulaDarah, Toast.LENGTH_SHORT).show();
                /*progressDialog = new ProgressDialog(NextUpdateKaloriActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Tunggu...");
                progressDialog.show();

                Retrofit retrofit = RetrofitUtil.getClient();
                KaloriApi kaloriApi = retrofit.create(KaloriApi.class);
                Call<ResponseComponent> call = kaloriApi.updateKalori(id, namaMakanan, jenisOlahraga, durasiOlahraga);
                call.enqueue(new Callback<ResponseComponent>() {
                    @Override
                    public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                        String error = response.body().getError();
                        String status = response.body().getStatus();
                        progressDialog.dismiss();
                        if(error.equals("1")) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NextUpdateKaloriActivity.this);
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
                            Toast.makeText(NextUpdateKaloriActivity.this, status, Toast.LENGTH_SHORT).show();
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
                });*/
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
