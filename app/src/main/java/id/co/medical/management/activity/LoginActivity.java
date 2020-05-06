package id.co.medical.management.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.medical.management.R;
import id.co.medical.management.api.AuthApi;
import id.co.medical.management.component.RecordsComponent;
import id.co.medical.management.component.ResponseComponent;
import id.co.medical.management.component.SharedPreferencesComponent;
import id.co.medical.management.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id._input_nomor_hp) EditText inputNomorHP;
    @BindView(R.id._btn_login) Button btnLogin;
    @BindView(R.id._btn_register) Button btnRegister;

    ProgressDialog progressDialog;
    private List<RecordsComponent> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(LoginActivity.this);

        /*check session*/
        if(SharedPreferencesComponent.getLoggedStatus(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin();
        btnRegister();
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

    private void btnLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomorHP = inputNomorHP.getText().toString().trim();

                if(nomorHP.equals("")){
                    Toast.makeText(LoginActivity.this, "Nomor HP harus diisi!", Toast.LENGTH_LONG).show();
                    inputNomorHP.setError("Nomor HP harus diisi!");
                    inputNomorHP.requestFocus();
                }else{
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Tunggu...");
                    progressDialog.show();

                    Retrofit retrofit = RetrofitUtil.getClient();
                    AuthApi authApi = retrofit.create(AuthApi.class);
                    Call<ResponseComponent> call = authApi.login(nomorHP);
                    call.enqueue(new Callback<ResponseComponent>() {
                        @Override
                        public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                            assert response.body() != null;
                            String error = response.body().getError();
                            String status = response.body().getStatus();
                            records = response.body().getRecords();
                            progressDialog.dismiss();
                            if(response.isSuccessful()){
                                if(error.equals("1")) {
                                    if(records != null) {
                                        SharedPreferencesComponent.setLoggedIn(LoginActivity.this, true);
                                        RecordsComponent recordsComponent = records.get(0);
                                        String id = recordsComponent.getId();
                                        new SharedPreferencesComponent(LoginActivity.this).setDataIn(id);

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }else{
                                    Toast.makeText(LoginActivity.this, status, Toast.LENGTH_SHORT).show();
                                }
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

}
