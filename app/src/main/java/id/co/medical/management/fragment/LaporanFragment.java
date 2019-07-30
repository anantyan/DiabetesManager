package id.co.medical.management.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import id.co.medical.management.R;
import id.co.medical.management.activity.LaporanCetakActivity;
import id.co.medical.management.adapter.RecyclerAdapter;
import id.co.medical.management.api.MakananApi;
import id.co.medical.management.component.RecordsComponent;
import id.co.medical.management.component.ResponseComponent;
import id.co.medical.management.component.SharedPreferencesComponent;
import id.co.medical.management.listener.RecyclerOnClickListener;
import id.co.medical.management.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LaporanFragment extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    View rootView, rootLayout;
    Button btnCetak;

    private List<RecordsComponent> records = new ArrayList<>();
    private RecyclerAdapter recyclerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_laporan, container, false);
        rootLayout = (CoordinatorLayout) rootView.findViewById(R.id._laporan);
        progressBar = (ProgressBar) rootView.findViewById(R.id._progress_bar);
        recyclerView = (RecyclerView) rootView.findViewById(R.id._select_data);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id._swipe_refresh);
        btnCetak = (Button) rootView.findViewById(R.id.btn_cetak);

        recyclerAdapter = new RecyclerAdapter(this.getActivity(), records);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.rootView.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(recyclerAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //LoadData
                loadData(new SharedPreferencesComponent(getActivity()).getActionMenu());

                //StopAnimate with Delay
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        onclickRecycler();
        btnCetak();
        return rootView;
    }

    public void btnCetak() {
        btnCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LaporanCetakActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onclickRecycler() {
        recyclerView.addOnItemTouchListener(new RecyclerOnClickListener(this.getActivity(), recyclerView, new RecyclerOnClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                /*//PassingData
                final RecordsComponent recordsComponent = records.get(position);

                //OnClick LoadData
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
                alertDialogBuilder.setTitle("Waktu: "+recordsComponent.getJam());
                alertDialogBuilder
                        .setMessage(
                                        "Olahraga: "+recordsComponent.getJenisOlahraga()+"\n" +
                                        "Durasi Olahraga: "+recordsComponent.getDurasiOlahraga()+"\n" +
                                        "Cek Gula darah: "+recordsComponent.getCekGula()+"\n" +
                                        "Hasil Cek Gula darah: "+recordsComponent.getHasilCekGula())
                        .setCancelable(false)
                        .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();*/
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    private void loadData(String getData) {

        String id = new SharedPreferencesComponent(this.getActivity()).getDataId();

        Retrofit retrofit = RetrofitUtil.getClient();
        MakananApi makananApi = retrofit.create(MakananApi.class);
        Call<ResponseComponent> call = makananApi.selectLaporan(id);

        switch (getData){
            case "nama":
                call.enqueue(new Callback<ResponseComponent>() {
                    @Override
                    public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        records = response.body() != null ? response.body().getRecords() : null;
                        if (response.isSuccessful()) {
                            if(records != null){
                                Collections.sort(records, new Comparator<RecordsComponent>() {
                                    @Override
                                    public int compare(RecordsComponent o1, RecordsComponent o2) {
                                        return o1.getNamaMakanan().compareTo(o2.getNamaMakanan());
                                    }
                                });
                                recyclerAdapter = new RecyclerAdapter(getActivity(), records);
                                recyclerView.setAdapter(recyclerAdapter);
                            }else{
                                Snackbar sn = Snackbar.make(rootLayout, "Data kosong!", Snackbar.LENGTH_LONG);
                                sn.getView();
                                sn.setAction("Oke", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                sn.setDuration(3000);
                                sn.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseComponent> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar sn = Snackbar.make(rootLayout, "Kesalahan jaringan!", Snackbar.LENGTH_LONG);
                        sn.getView();
                        sn.setAction("Oke", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        sn.setDuration(3000);
                        sn.show();
                    }
                });
                break;
            case "kalori":
                call.enqueue(new Callback<ResponseComponent>() {
                    @Override
                    public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        records = response.body() != null ? response.body().getRecords() : null;
                        if (response.isSuccessful()) {
                            if(records != null){
                                Collections.sort(records, new Comparator<RecordsComponent>() {
                                    @Override
                                    public int compare(RecordsComponent o1, RecordsComponent o2) {
                                        String a = o1.getTargetKalori();
                                        String b = o2.getTargetKalori();
                                        return Integer.parseInt(a)- Integer.parseInt(b);
                                    }
                                });
                                recyclerAdapter = new RecyclerAdapter(getActivity(), records);
                                recyclerView.setAdapter(recyclerAdapter);
                            }else{
                                Snackbar sn = Snackbar.make(rootLayout, "Data kosong!", Snackbar.LENGTH_LONG);
                                sn.getView();
                                sn.setAction("Oke", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                sn.setDuration(3000);
                                sn.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseComponent> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar sn = Snackbar.make(rootLayout, "Kesalahan jaringan!", Snackbar.LENGTH_LONG);
                        sn.getView();
                        sn.setAction("Oke", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        sn.setDuration(3000);
                        sn.show();
                    }
                });
                break;
            case "Nomor":
                call.enqueue(new Callback<ResponseComponent>() {
                    @Override
                    public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        records = response.body() != null ? response.body().getRecords() : null;
                        if (response.isSuccessful()) {
                            if(records != null){
                                Collections.sort(records, new Comparator<RecordsComponent>() {
                                    @Override
                                    public int compare(RecordsComponent o1, RecordsComponent o2) {
                                        String a = o1.getNomor();
                                        String b = o2.getNomor();
                                        return Integer.parseInt(a)- Integer.parseInt(b);
                                    }
                                });
                                recyclerAdapter = new RecyclerAdapter(getActivity(), records);
                                recyclerView.setAdapter(recyclerAdapter);
                            }else{
                                Snackbar sn = Snackbar.make(rootLayout, "Data kosong!", Snackbar.LENGTH_LONG);
                                sn.getView();
                                sn.setAction("Oke", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                sn.setDuration(3000);
                                sn.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseComponent> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar sn = Snackbar.make(rootLayout, "Kesalahan jaringan!", Snackbar.LENGTH_LONG);
                        sn.getView();
                        sn.setAction("Oke", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        sn.setDuration(3000);
                        sn.show();
                    }
                });
                break;
            default:
                call.enqueue(new Callback<ResponseComponent>() {
                    @Override
                    public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        records = response.body() != null ? response.body().getRecords() : null;
                        if (response.isSuccessful()) {
                            if(records != null){
                                recyclerAdapter = new RecyclerAdapter(getActivity(), records);
                                recyclerView.setAdapter(recyclerAdapter);
                            }else{
                                Snackbar sn = Snackbar.make(rootLayout, "Data kosong!", Snackbar.LENGTH_LONG);
                                sn.getView();
                                sn.setAction("Oke", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                sn.setDuration(3000);
                                sn.show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseComponent> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar sn = Snackbar.make(rootLayout, "Kesalahan jaringan!", Snackbar.LENGTH_LONG);
                        sn.getView();
                        sn.setAction("Oke", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        sn.setDuration(3000);
                        sn.show();
                    }
                });
                break;
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_laporan, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_sortNama){
            loadData("nama");
            new SharedPreferencesComponent(this.getActivity()).setActionMenuOut();
            new SharedPreferencesComponent(this.getActivity()).setActionMenuIn("nama");
        }else if(id == R.id.action_sortKalori){
            loadData("kalori");
            new SharedPreferencesComponent(this.getActivity()).setActionMenuOut();
            new SharedPreferencesComponent(this.getActivity()).setActionMenuIn("kalori");
        }else if(id == R.id.action_sortNomor){
            loadData("nomor");
            new SharedPreferencesComponent(this.getActivity()).setActionMenuOut();
            new SharedPreferencesComponent(this.getActivity()).setActionMenuIn("nomor");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(new SharedPreferencesComponent(this.getActivity()).getActionMenu());
    }
}
