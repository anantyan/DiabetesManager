package com.diabetes.manager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.diabetes.manager.api.AuthApi;
import com.diabetes.manager.api.KaloriApi;
import com.diabetes.manager.R;
import com.diabetes.manager.component.RecordsComponent;
import com.diabetes.manager.component.ResponseComponent;
import com.diabetes.manager.component.SharedPreferenceComponent;
import com.diabetes.manager.util.RetrofitUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DashboardActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id._txt_kalori) TextView txtKalori;
    @BindView(R.id._txt_id) TextView txtId;

    private List<RecordsComponent> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(DashboardActivity.this);

        setSupportActionBar(toolbar);

        getId();
        getKalori();

        /*hide txtId*/
        txtId.setVisibility(View.INVISIBLE);
    }

    private void getId() {

        Retrofit retrofit = RetrofitUtil.getClient();
        AuthApi authApi = retrofit.create(AuthApi.class);
        Call<ResponseComponent> call = authApi.getData();
        call.enqueue(new Callback<ResponseComponent>() {

            @Override
            public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                records = response.body().getRecords();
                if (response.isSuccessful()) {
                    final RecordsComponent recordsComponent = records.get(0);

                    String id = recordsComponent.getId();
                    String kalori = recordsComponent.getKalori();

                    /*save in shared preference*/
                    new SharedPreferenceComponent(DashboardActivity.this).setDataIn(id);

                    txtId.setText(id);
                    txtKalori.setText(kalori);
                }
            }

            @Override
            public void onFailure(Call<ResponseComponent> call, Throwable t) {

            }
        });
    }

    private void getKalori() {

        Retrofit retrofit = RetrofitUtil.getClient();
        KaloriApi kaloriApi = retrofit.create(KaloriApi.class);
        Call<ResponseComponent> call = kaloriApi.selectKalori(new SharedPreferenceComponent(DashboardActivity.this).getDataId());
        call.enqueue(new Callback<ResponseComponent>() {
            @Override
            public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                records = response.body().getRecords();
                if (response.isSuccessful()) {
                    final RecordsComponent recordsComponent = records.get(0);

                    String id = recordsComponent.getId();
                    String kalori = recordsComponent.getKalori();

                    txtId.setText(id);
                    txtKalori.setText(kalori);
                }
            }

            @Override
            public void onFailure(Call<ResponseComponent> call, Throwable t) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_inputKalori) {
            Intent intent = new Intent(DashboardActivity.this, InputKaloriActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_updateKalori){
            Intent intent = new Intent(DashboardActivity.this, UpdateKaloriActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getId();
        getKalori();
    }
}
