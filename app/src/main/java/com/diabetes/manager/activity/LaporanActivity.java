package com.diabetes.manager.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.diabetes.manager.R;
import com.diabetes.manager.adapter.RecyclerAdapter;
import com.diabetes.manager.api.MakananApi;
import com.diabetes.manager.component.RecordsComponent;
import com.diabetes.manager.component.ResponseComponent;
import com.diabetes.manager.component.SharedPreferenceComponent;
import com.diabetes.manager.util.RetrofitUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LaporanActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id._select_data) RecyclerView recyclerView;
    @BindView(R.id._progress_bar) ProgressBar progressBar;
    @BindView(R.id._swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;

    private List<RecordsComponent> records = new ArrayList<>();
    private RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);
        ButterKnife.bind(LaporanActivity.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerAdapter = new RecyclerAdapter(LaporanActivity.this, records);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LaporanActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(LaporanActivity.this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(recyclerAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {

                        //LoadData
                        loadData(new SharedPreferenceComponent(LaporanActivity.this).getActionMenu());

                        //StopAnimate with Delay
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(

                android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_dark
        );
    }

    private void loadData(String getData) {
        String id = new SharedPreferenceComponent(LaporanActivity.this).getDataId();

        Retrofit retrofit = RetrofitUtil.getClient();
        MakananApi makananApi = retrofit.create(MakananApi.class);
        Call<ResponseComponent> call = makananApi.selectLaporan(id);

        switch (getData){
            case "nama":
                call.enqueue(new Callback<ResponseComponent>() {
                    private void sortData() {
                        Collections.sort(records, new Comparator<RecordsComponent>() {
                            @Override
                            public int compare(RecordsComponent o1, RecordsComponent o2) {
                                return o1.getNamaMakanan().compareTo(o2.getNamaMakanan());
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        records = response.body().getRecords();
                        sortData();
                        if (response.isSuccessful()) {
                            recyclerAdapter = new RecyclerAdapter(LaporanActivity.this, records);
                            recyclerView.setAdapter(recyclerAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseComponent> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(findViewById(R.id._laporan), "Data kosong! atau Kesalahan jaringan!", Snackbar.LENGTH_LONG)
                                .setAction("Oke", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .setDuration(3000)
                                .show();
                    }
                });
                break;
            case "kalori":
                call.enqueue(new Callback<ResponseComponent>() {
                    private void sortData() {
                        Collections.sort(records, new Comparator<RecordsComponent>() {
                            @Override
                            public int compare(RecordsComponent o1, RecordsComponent o2) {
                                return o1.getTargetKalori().compareTo(o2.getTargetKalori());
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        records = response.body().getRecords();
                        sortData();
                        if (response.isSuccessful()) {
                            recyclerAdapter = new RecyclerAdapter(LaporanActivity.this, records);
                            recyclerView.setAdapter(recyclerAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseComponent> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(findViewById(R.id._laporan), "Data kosong! atau Kesalahan jaringan!", Snackbar.LENGTH_LONG)
                                .setAction("Oke", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .setDuration(3000)
                                .show();
                    }
                });
                break;
            default:
                call.enqueue(new Callback<ResponseComponent>() {
                    @Override
                    public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        records = response.body().getRecords();
                        if (response.isSuccessful()) {
                            recyclerAdapter = new RecyclerAdapter(LaporanActivity.this, records);
                            recyclerView.setAdapter(recyclerAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseComponent> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(findViewById(R.id._laporan), "Data kosong! atau Kesalahan jaringan!", Snackbar.LENGTH_LONG)
                                .setAction("Oke", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                })
                                .setDuration(3000)
                                .show();
                    }
                });
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_laporan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_sortNama) {
            loadData("nama");
            new SharedPreferenceComponent(LaporanActivity.this).setActionMenuOut();
            new SharedPreferenceComponent(LaporanActivity.this).setActionMenuIn("nama");
        }else if(id == R.id.action_default){
            loadData("");
            new SharedPreferenceComponent(LaporanActivity.this).setActionMenuOut();
            new SharedPreferenceComponent(LaporanActivity.this).setActionMenuIn("");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(new SharedPreferenceComponent(LaporanActivity.this).getActionMenu());
    }
}
