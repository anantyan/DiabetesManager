package id.co.medical.management.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.co.medical.management.R;
import id.co.medical.management.api.KaloriApi;
import id.co.medical.management.api.MakananApi;
import id.co.medical.management.component.RecordsComponent;
import id.co.medical.management.component.ResponseComponent;
import id.co.medical.management.component.SharedPreferencesComponent;
import id.co.medical.management.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MakananFragment extends Fragment {

    Spinner inputMakanan;
    Button btnSave;
    ProgressDialog progressDialog;
    View rootView, rootLayout;

    private List<RecordsComponent> records;

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
        inputMakanan = (Spinner) rootView.findViewById(R.id._input_makanan);
        btnSave = (Button) rootView.findViewById(R.id._btn_save);

        loadDataSpinner();
        buttonNextData();

        return rootView;
    }

    private void buttonNextData() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = new SharedPreferencesComponent(rootView.getContext()).getDataId();
                String namaMakanan = String.valueOf(inputMakanan.getSelectedItem().toString().trim());

                if(namaMakanan.equals("Pilihan...")){
                    Toast.makeText(getActivity(),
                            "Nama Makanan harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputMakanan.requestFocus();
                }else{
                    Retrofit retrofit = RetrofitUtil.getClient();
                    KaloriApi kaloriApi = retrofit.create(KaloriApi.class);
                    Call<ResponseComponent> call = kaloriApi.updateKalori(id, namaMakanan);
                    call.enqueue(new Callback<ResponseComponent>() {
                        @Override
                        public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                            assert response.body() != null;
                            String error = response.body().getError();
                            String status = response.body().getStatus();
                            progressDialog.dismiss();
                            if(error.equals("1")) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
                                alertDialogBuilder.setTitle("Peringatan!");
                                alertDialogBuilder
                                        .setMessage(status)
                                        .setCancelable(false)
                                        .setPositiveButton("Iya",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            } else {
                                Toast.makeText(rootView.getContext(), status, Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseComponent> call, Throwable t) {
                            progressDialog.dismiss();
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
                }
            }
        });
    }

    private void loadDataSpinner() {
        final List<String> listMakanan = new ArrayList<>();
        listMakanan.add(0, "Pilihan...");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.rootView.getContext(), android.R.layout.simple_spinner_item, listMakanan){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                } else {
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
        inputMakanan.setAdapter(adapter);

        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Tunggu...");
        progressDialog.show();

        Retrofit retrofit = RetrofitUtil.getClient();
        MakananApi makananApi = retrofit.create(MakananApi.class);
        Call<ResponseComponent> call = makananApi.selectMakanan();
        call.enqueue(new Callback<ResponseComponent>() {
            @Override
            public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                progressDialog.dismiss();
                records = response.body() != null ? response.body().getRecords() : null;
                if(response.isSuccessful()){
                    /*select key & value spinner*/
                    for (int i = 0; i < records.size(); i++){
                        listMakanan.add(records.get(i).getNamaMakanan());
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseComponent> call, Throwable t) {
                progressDialog.dismiss();
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_makanan, menu);
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
                            "Applications version 1.0 BETA")
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
}
