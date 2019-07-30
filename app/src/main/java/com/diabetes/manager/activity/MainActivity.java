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
import com.diabetes.manager.component.SharedPreferenceComponent;
import com.diabetes.manager.util.RetrofitUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id._input_nomor_hp) EditText inputNomorHP;
    @BindView(R.id._btn_login) Button btnLogin;
    @BindView(R.id._btn_register) Button btnRegister;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);

        /*check session*/
        if(SharedPreferenceComponent.getLoggedStatus(getApplicationContext())) {
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin();
        btnRegister();
    }

    private void btnLogin() {

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomorHP = inputNomorHP.getText().toString().trim();

                if(nomorHP.equals("")){
                    Toast.makeText(MainActivity.this, "Nomor HP harus diisi!", Toast.LENGTH_LONG).show();
                    inputNomorHP.setError("Nomor HP harus diisi!");
                    inputNomorHP.requestFocus();
                }else{
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Tunggu...");
                    progressDialog.show();

                    Retrofit retrofit = RetrofitUtil.getClient();
                    AuthApi authApi = retrofit.create(AuthApi.class);
                    Call<ResponseComponent> call = authApi.login(nomorHP);
                    call.enqueue(new Callback<ResponseComponent>() {
                        @Override
                        public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                            String error = response.body().getError();
                            String status = response.body().getStatus();
                            progressDialog.dismiss();
                            if(error.equals("1")) {
                                SharedPreferenceComponent.setLoggedIn(getApplicationContext(), true);
                                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseComponent> call, Throwable t) {

                            progressDialog.dismiss();
                            Snackbar.make(findViewById(R.id._login), "Kesalahan pada jaringan!", Snackbar.LENGTH_LONG)
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

    private void btnRegister() {

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
