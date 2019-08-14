package id.co.medical.management.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import id.co.medical.management.R;
import id.co.medical.management.activity.LaporanCetakActivity;
import id.co.medical.management.activity.MainActivity;
import id.co.medical.management.adapter.LaporanAdapter;
import id.co.medical.management.api.MakananApi;
import id.co.medical.management.component.CekDataComponent;
import id.co.medical.management.component.RecordsComponent;
import id.co.medical.management.component.ResponseComponent;
import id.co.medical.management.component.SharedPreferencesComponent;
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
    Button btnCetak, btnNext, btnPrev, btnCalendar;
    DatePickerDialog datePickerDialog;
    TextView txtDataKosong;

    //Calendar
    Calendar calendar = Calendar.getInstance();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String valueDate = simpleDateFormat.format(calendar.getTime());
    int mYear, mMonth, mDay;

    private List<RecordsComponent> records = new ArrayList<>();
    private LaporanAdapter laporanAdapter;

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
        btnNext = (Button) rootView.findViewById(R.id._btn_next);
        btnPrev = (Button) rootView.findViewById(R.id._btn_prev);
        btnCalendar = (Button) rootView.findViewById(R.id._btn_calendar);
        txtDataKosong = (TextView) rootView.findViewById(R.id._txt_data_kosong);

        String id = new SharedPreferencesComponent(this.getActivity()).getDataId(); // get data id
        new CekDataComponent(this.getActivity(), rootLayout).CekDataId(id); // cek apakah akun terblokir atau tidak

        laporanAdapter = new LaporanAdapter(this.getActivity(), records);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.rootView.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(laporanAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //LoadData + DateOfDate
                loadData(valueDate);

                //StopAnimate with Delay
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        onclickRecycler();
        btnCetak();
        btnNext();
        btnPrev();
        btnCalendar();
        txtDataKosong.setVisibility(View.INVISIBLE);
        return rootView;
    }

    private void btnCalendar() {
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYear   = calendar.get(Calendar.YEAR); //menggunakan cara simple & custom datepicker dialog
                mMonth  = calendar.get(Calendar.MONTH); //menggunakan cara simple & custom datepicker dialog
                mDay    = calendar.get(Calendar.DAY_OF_MONTH); //menggunakan cara simple & custom datepicker dialog
                datePickerDialog = new DatePickerDialog(rootView.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                mYear   = year;
                                mMonth  = month;
                                mDay    = dayOfMonth;
                                loadData(mYear+"-"+(mMonth + 1)+"-"+mDay); //menggunakan cara simple & custom datepicker dialog
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void btnNext() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DATE, 1);
                valueDate = simpleDateFormat.format(calendar.getTime());
                loadData(valueDate);
            }
        });
    }

    private void btnPrev() {
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DATE, -1);
                valueDate = simpleDateFormat.format(calendar.getTime());
                loadData(valueDate);
            }
        });
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

    }

    private void loadData(String date)  {

        String id = new SharedPreferencesComponent(this.getActivity()).getDataId();

        Retrofit retrofit = RetrofitUtil.getClient();
        MakananApi makananApi = retrofit.create(MakananApi.class);
        Call<ResponseComponent> call = makananApi.selectLaporan(id, date);
        call.enqueue(new Callback<ResponseComponent>() {
            @Override
            public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                records = response.body() != null ? response.body().getRecords() : null;
                if (response.isSuccessful()) {
                    if(records != null){
                        laporanAdapter = new LaporanAdapter(getActivity(), records);
                        recyclerView.setAdapter(laporanAdapter);
                        txtDataKosong.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }else{
                        txtDataKosong.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
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
                recyclerView.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_laporan, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_about){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.rootView.getContext());
            alertDialogBuilder.setTitle("Tentang Aplikasi");
            alertDialogBuilder
                    .setMessage("Diabetes Manager\n" +
                            "Applications version "+getString(R.string.version))
                    .setCancelable(false)
                    .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(valueDate);
    }
}
