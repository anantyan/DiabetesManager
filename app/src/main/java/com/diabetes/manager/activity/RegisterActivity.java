package com.diabetes.manager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diabetes.manager.api.AuthApi;
import com.diabetes.manager.R;
import com.diabetes.manager.component.ResponseComponent;
import com.diabetes.manager.util.RetrofitUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id._input_nomor_hp) EditText inputNomorHP;
    @BindView(R.id._input_re_nomor_hp) EditText inputReNomorHP;
    @BindView(R.id._btn_register) Button btnRegister;
    @BindView(R.id._btn_login) Button btnLogin;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(RegisterActivity.this);

        btnRegister();
        btnLogin();
    }

    private void btnLogin() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void btnRegister() {

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomorHP = inputNomorHP.getText().toString().trim();
                String reNomorHP = inputReNomorHP.getText().toString().trim();

                if(nomorHP.equals("")){
                    Toast.makeText(RegisterActivity.this, "Nomor HP harus diisi!", Toast.LENGTH_LONG).show();
                    inputNomorHP.setError("Nomor HP harus diisi!");
                    inputNomorHP.requestFocus();
                }else if(reNomorHP.equals("")){
                    Toast.makeText(RegisterActivity.this, "Re-Input Nomor HP harus diisi!", Toast.LENGTH_LONG).show();
                    inputReNomorHP.setError("Re-Input Nomor HP harus diisi!");
                    inputReNomorHP.requestFocus();
                }else{
                    progressDialog = new ProgressDialog(RegisterActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Tunggu...");
                    progressDialog.show();

                    Retrofit retrofit = RetrofitUtil.getClient();
                    AuthApi authApi = retrofit.create(AuthApi.class);
                    Call<ResponseComponent> call = authApi.register(nomorHP, reNomorHP);
                    call.enqueue(new Callback<ResponseComponent>() {

                        @Override
                        public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                            String error = response.body().getError();
                            String status = response.body().getStatus();
                            progressDialog.dismiss();
                            if(error.equals("1")) {
                                Toast.makeText(RegisterActivity.this, status, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, status, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseComponent> call, Throwable t) {
                            progressDialog.dismiss();
                            Snackbar.make(findViewById(R.id._register), "Kesalahan pada jaringan!", Snackbar.LENGTH_LONG)
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
}
