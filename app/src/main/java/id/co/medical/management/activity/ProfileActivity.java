package id.co.medical.management.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.medical.management.R;
import id.co.medical.management.api.AuthApi;
import id.co.medical.management.api.KaloriApi;
import id.co.medical.management.component.ResponseComponent;
import id.co.medical.management.component.SharedPreferencesComponent;
import id.co.medical.management.utils.LoadImageUtil;
import id.co.medical.management.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_photo)
    ImageView profilePhoto;
    @BindView(R.id.profile_name)
    TextView profileName;
    @BindView(R.id.profile_nomorhp)
    TextView profileNomorHP;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_save_name)
    Button btnSaveName;
    @BindView(R.id.btn_save_nomor)
    Button btnSaveNomor;
    @BindView(R.id.btn_save_pin)
    Button btnSavePin;

    ProgressDialog progressDialog;

    public static final String EXTRA_NAME_PROFILE = "extra_name_profile";
    public static final String EXTRA_NOMORHP_PROFILE = "extra_nomorhp_profile";
    public static final String EXTRA_PHOTO_PROFILE = "extra_photo_profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String name = getIntent().getStringExtra(EXTRA_NAME_PROFILE);
        String nomorHP = getIntent().getStringExtra(EXTRA_NOMORHP_PROFILE);
        String photoProfile = getIntent().getStringExtra(EXTRA_PHOTO_PROFILE);

        profileName.setText(name);
        profileNomorHP.setText(nomorHP);
        new LoadImageUtil(ProfileActivity.this).setGlideWithAccent(photoProfile, profilePhoto);

        btnSaveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_dialog_profile_name, null);

                String txt_name = profileName.getText().toString().trim();
                String txt_id = new SharedPreferencesComponent(ProfileActivity.this).getDataId();

                EditText input_name = (EditText) dialogView.findViewById(R.id.profile_name);
                input_name.setText(txt_name);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
                alertDialogBuilder
                        .setTitle("Edit Nama")
                        .setView(dialogView)
                        .setCancelable(false)
                        .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String name = input_name.getText().toString().trim();

                                if(name.equals("")){
                                    Toast.makeText(getApplicationContext(),
                                            "Nama harus diisi!",
                                            Toast.LENGTH_LONG).show();
                                    input_name.setError("Nama harus diisi!");
                                    input_name.requestFocus();
                                }else{
                                    //Toast.makeText(ProfileActivity.this, name, Toast.LENGTH_SHORT).show();
                                    progressDialog = new ProgressDialog(ProfileActivity.this);
                                    progressDialog.setCancelable(false);
                                    progressDialog.setMessage("Tunggu...");
                                    progressDialog.show();

                                    Retrofit retrofit = RetrofitUtil.getClient();
                                    AuthApi authApi = retrofit.create(AuthApi.class);
                                    Call<ResponseComponent> call = authApi.profileUpdateName(txt_id, name);
                                    call.enqueue(new Callback<ResponseComponent>() {
                                        @Override
                                        public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                                            assert response.body() != null;
                                            String error = response.body().getError();
                                            String status = response.body().getStatus();
                                            progressDialog.dismiss();
                                            if(response.isSuccessful()){
                                                if(error != null){
                                                    if(error.equals("1")){
                                                        Toast.makeText(ProfileActivity.this, status, Toast.LENGTH_SHORT).show();
                                                        profileName.setText(name);
                                                    }else{
                                                        Toast.makeText(ProfileActivity.this, status, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseComponent> call, Throwable t) {
                                            progressDialog.dismiss();
                                            Snackbar.make(findViewById(R.id.activity_profile), "Kesalahan pada jaringan!", Snackbar.LENGTH_LONG)
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
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        btnSaveNomor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_dialog_profile_nomorhp, null);

                String txt_nomorhp = profileNomorHP.getText().toString().trim();
                String txt_id = new SharedPreferencesComponent(ProfileActivity.this).getDataId();

                EditText input_nomorhp = (EditText) dialogView.findViewById(R.id.profile_nomorhp);
                EditText input_pin = (EditText) dialogView.findViewById(R.id.profile_pin);
                input_nomorhp.setText(txt_nomorhp);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
                alertDialogBuilder
                        .setTitle("Edit Nomor HP")
                        .setView(dialogView)
                        .setCancelable(false)
                        .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String nomorhp = input_nomorhp.getText().toString().trim();
                                String pin = input_pin.getText().toString().trim();

                                if(nomorhp.equals("")){
                                    Toast.makeText(getApplicationContext(),
                                            "Nomor HP harus diisi!",
                                            Toast.LENGTH_LONG).show();
                                    input_nomorhp.setError("Nomor HP harus diisi!");
                                    input_nomorhp.requestFocus();
                                }else if(pin.equals("")){
                                    Toast.makeText(getApplicationContext(),
                                            "Pin harus diisi!",
                                            Toast.LENGTH_LONG).show();
                                    input_pin.setError("Pin harus diisi!");
                                    input_pin.requestFocus();
                                }else{
                                    //Toast.makeText(ProfileActivity.this, name, Toast.LENGTH_SHORT).show();
                                    progressDialog = new ProgressDialog(ProfileActivity.this);
                                    progressDialog.setCancelable(false);
                                    progressDialog.setMessage("Tunggu...");
                                    progressDialog.show();

                                    Retrofit retrofit = RetrofitUtil.getClient();
                                    AuthApi authApi = retrofit.create(AuthApi.class);
                                    Call<ResponseComponent> call = authApi.profileUpdateNomor(txt_id, nomorhp, pin);
                                    call.enqueue(new Callback<ResponseComponent>() {
                                        @Override
                                        public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                                            assert response.body() != null;
                                            String error = response.body().getError();
                                            String status = response.body().getStatus();
                                            progressDialog.dismiss();
                                            if(response.isSuccessful()){
                                                if(error != null){
                                                    if(error.equals("1")){
                                                        Toast.makeText(ProfileActivity.this, status, Toast.LENGTH_SHORT).show();
                                                        profileNomorHP.setText(nomorhp);
                                                    }else{
                                                        Toast.makeText(ProfileActivity.this, status, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseComponent> call, Throwable t) {
                                            progressDialog.dismiss();
                                            Snackbar.make(findViewById(R.id.activity_profile), "Kesalahan pada jaringan!", Snackbar.LENGTH_LONG)
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
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        btnSavePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
                alertDialogBuilder
                        .setTitle("Edit Pin")
                        .setCancelable(false)
                        .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
