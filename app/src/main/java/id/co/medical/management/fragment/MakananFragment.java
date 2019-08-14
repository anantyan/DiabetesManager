package id.co.medical.management.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import id.co.medical.management.R;
import id.co.medical.management.adapter.MakananAdapter;
import id.co.medical.management.api.KaloriApi;
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

public class MakananFragment extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    View rootView, rootLayout;
    MakananAdapter makananAdapter;
    MenuItem searchItem;
    SearchView searchView;

    private List<RecordsComponent> records = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_makanan, container, false);
        rootLayout = (CoordinatorLayout) rootView.findViewById(R.id._makanan);
        progressBar = (ProgressBar) rootView.findViewById(R.id._progress_bar);
        recyclerView = (RecyclerView) rootView.findViewById(R.id._select_data);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id._swipe_refresh);

        String id = new SharedPreferencesComponent(this.getActivity()).getDataId(); // get data id
        new CekDataComponent(this.getActivity(), rootLayout).CekDataId(id); // cek apakah akun terblokir atau tidak

        makananAdapter = new MakananAdapter(this.getActivity(), records);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.rootView.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(makananAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //LoadData
                loadData(new SharedPreferencesComponent(getActivity()).getActionMakanan());

                //StopAnimate with Delay
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        onclickRecycler();
        return rootView;
    }

    private void onclickRecycler() {
        // Untuk click pada recyclerView
    }

    private void loadData(String getData) {
        Retrofit retrofit = RetrofitUtil.getClient();
        MakananApi makananApi = retrofit.create(MakananApi.class);
        Call<ResponseComponent> call = makananApi.selectMakanan();
        switch (getData) {
            case "nama":
                call.enqueue(new Callback<ResponseComponent>() {
                    @Override
                    public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        records = response.body() != null ? response.body().getRecords() : null;
                        if (response.isSuccessful()) {
                            if (records != null) {
                                Collections.sort(records, new Comparator<RecordsComponent>() {
                                    @Override
                                    public int compare(RecordsComponent o1, RecordsComponent o2) {
                                        return o1.getNamaMakanan().compareTo(o2.getNamaMakanan());
                                    }
                                });
                                makananAdapter = new MakananAdapter(getActivity(), records);
                                recyclerView.setAdapter(makananAdapter);
                            } else {
                                Snackbar.make(rootLayout, "Data kosong!", Snackbar.LENGTH_LONG)
                                        .setAction("Oke", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        })
                                        .setDuration(3000)
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseComponent> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(rootLayout, "Kesalahan pada jaringan!", Snackbar.LENGTH_LONG)
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
                    @Override
                    public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        records = response.body() != null ? response.body().getRecords() : null;
                        if (response.isSuccessful()) {
                            if (records != null) {
                                Collections.sort(records, new Comparator<RecordsComponent>() {
                                    @Override
                                    public int compare(RecordsComponent o1, RecordsComponent o2) {
                                        String a = o1.getJmlKalori();
                                        String b = o2.getJmlKalori();
                                        return (int) (Double.parseDouble(a)- Double.parseDouble(b));
                                    }
                                });
                                makananAdapter = new MakananAdapter(getActivity(), records);
                                recyclerView.setAdapter(makananAdapter);
                            } else {
                                Snackbar.make(rootLayout, "Data kosong!", Snackbar.LENGTH_LONG)
                                        .setAction("Oke", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        })
                                        .setDuration(3000)
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseComponent> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(rootLayout, "Kesalahan pada jaringan!", Snackbar.LENGTH_LONG)
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
                        records = response.body() != null ? response.body().getRecords() : null;
                        if (response.isSuccessful()) {
                            if (records != null) {
                                Collections.sort(records, new Comparator<RecordsComponent>() {
                                    @Override
                                    public int compare(RecordsComponent o1, RecordsComponent o2) {
                                        String a = o1.getJmlKalori();
                                        String b = o2.getJmlKalori();
                                        return (int) (Double.parseDouble(a)- Double.parseDouble(b));
                                    }
                                });
                                makananAdapter = new MakananAdapter(getActivity(), records);
                                recyclerView.setAdapter(makananAdapter);
                            } else {
                                Snackbar.make(rootLayout, "Data kosong!", Snackbar.LENGTH_LONG)
                                        .setAction("Oke", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        })
                                        .setDuration(3000)
                                        .show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseComponent> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        Snackbar.make(rootLayout, "Kesalahan pada jaringan!", Snackbar.LENGTH_LONG)
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_makanan, menu);

        searchItem = menu.findItem(R.id.action_pencarian);
        searchView = (SearchView) searchItem.getActionView();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_sortNama){
            loadData("nama");
            new SharedPreferencesComponent(this.getActivity()).setActionMakananOut();
            new SharedPreferencesComponent(this.getActivity()).setActionMakananIn("nama");
        }else if(id == R.id.action_sortKalori){
            loadData("kalori");
            new SharedPreferencesComponent(this.getActivity()).setActionMakananOut();
            new SharedPreferencesComponent(this.getActivity()).setActionMakananIn("kalori");
        }else if(id == R.id.action_pencarian){
            searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    makananAdapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(new SharedPreferencesComponent(this.getActivity()).getActionMakanan());
    }
}