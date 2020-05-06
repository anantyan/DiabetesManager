package id.co.medical.management.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.co.medical.management.R;
import id.co.medical.management.api.KaloriApi;
import id.co.medical.management.component.CekDataComponent;
import id.co.medical.management.component.ResponseComponent;
import id.co.medical.management.component.SharedPreferencesComponent;
import id.co.medical.management.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class KaloriFragment extends Fragment {

    EditText inputUmur,inputTB, inputBB;
    Spinner inputJK, inputAktif, inputHamil;
    Button btnSave;
    ProgressDialog progressDialog;
    View rootView, rootLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_kalori, container, false);
        rootLayout = (CoordinatorLayout) rootView.findViewById(R.id._kalori);
        inputUmur = (EditText) rootView.findViewById(R.id._input_umur);
        inputTB = (EditText) rootView.findViewById(R.id._input_tb);
        inputBB = (EditText) rootView.findViewById(R.id._input_bb);
        inputJK = (Spinner) rootView.findViewById(R.id._input_jk);
        inputAktif = (Spinner) rootView.findViewById(R.id._input_aktif);
        inputHamil = (Spinner) rootView.findViewById(R.id._input_hamil);
        btnSave = (Button) rootView.findViewById(R.id._btn_save);

        String id = new SharedPreferencesComponent(this.getActivity()).getDataId(); // get data id
        new CekDataComponent(this.getActivity(), rootLayout).CekDataId(id); // cek apakah akun terblokir atau tidak

        spinnerJenisKelamin();
        buttonSave();

        return rootView;
    }

    private void buttonSave() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
                alertDialogBuilder.setTitle("Konfirmasi");
                alertDialogBuilder
                        .setMessage("Apakah anda ingin mengubah data?")
                        .setCancelable(false)
                        .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String dataId = new SharedPreferencesComponent(getActivity()).getDataId();

                                /*value for spinner*/
                                String[] valJenisKelamin = new String[]{"", "Perempuan", "Laki - laki"};
                                String[] valAktifitas = new String[]{"", "Ringan", "Sedang", "Berat"};
                                String[] valHamil = new String[]{"", "Trimester 1", "Trimester 2", "Trimester 3", "Menyusui"};

                                String umur = inputUmur.getText().toString().trim();
                                String tb = inputTB.getText().toString().trim();
                                String bb = inputBB.getText().toString().trim();
                                String jk = valJenisKelamin[inputJK.getSelectedItemPosition()];
                                String aktif = valAktifitas[inputAktif.getSelectedItemPosition()];
                                String hamil = valHamil[inputHamil.getSelectedItemPosition()];

                                if(jk.equals("")){
                                    Toast.makeText(getActivity(),
                                            "Jenis Kelamin harus diisi!",
                                            Toast.LENGTH_LONG).show();
                                    inputJK.requestFocus();
                                }else if(umur.equals("")){
                                    Toast.makeText(getActivity(),
                                            "Umur harus diisi!",
                                            Toast.LENGTH_LONG).show();
                                    inputUmur.setError("Umur harus diisi!");
                                    inputUmur.requestFocus();
                                }else if(tb.equals("")){
                                    Toast.makeText(getActivity(),
                                            "Tinggi Badan harus diisi!",
                                            Toast.LENGTH_LONG).show();
                                    inputTB.setError("Tinggi Badan harus diisi!");
                                    inputTB.requestFocus();
                                }else if(bb.equals("")){
                                    Toast.makeText(getActivity(),
                                            "Berat Badan harus diisi!",
                                            Toast.LENGTH_LONG).show();
                                    inputBB.setError("Berat Badan harus diisi!");
                                    inputBB.requestFocus();
                                }else if(aktif.equals("")){
                                    Toast.makeText(getActivity(),
                                            "Aktifitas harus diisi!",
                                            Toast.LENGTH_LONG).show();
                                    inputAktif.requestFocus();
                                }else{
                                    progressDialog = new ProgressDialog(getActivity());
                                    progressDialog.setCancelable(false);
                                    progressDialog.setMessage("Tunggu...");
                                    progressDialog.show();

                                    Retrofit retrofit = RetrofitUtil.getClient();
                                    KaloriApi kaloriApi = retrofit.create(KaloriApi.class);
                                    Call<ResponseComponent> call = kaloriApi.insertKalori(dataId, jk, umur, tb, bb, aktif, hamil);
                                    call.enqueue(new Callback<ResponseComponent>() {

                                        @Override
                                        public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                                            assert response.body() != null;
                                            String error = response.body().getError();
                                            String status = response.body().getStatus();
                                            progressDialog.dismiss();
                                            if(error.equals("1")) {
                                                Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();
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
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void spinnerJenisKelamin() {
        /*script untuk spinner jenis kelamin*/
        List<String> jenisKelamin = new ArrayList<>();
        jenisKelamin.add("Pilihan...");
        jenisKelamin.add("Perempuan");
        jenisKelamin.add("Laki - laki");
        ArrayAdapter<String> spinnerJenisKelamin = new ArrayAdapter<String>(
                this.rootView.getContext(),
                android.R.layout.simple_spinner_item, jenisKelamin){
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
        spinnerJenisKelamin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputJK.setAdapter(spinnerJenisKelamin);

        /*script untuk spinner aktifitas*/
        List<String> aktifitas = new ArrayList<>();
        aktifitas.add("Pilihan...");
        aktifitas.add("Ringan");
        aktifitas.add("Sedang");
        aktifitas.add("Berat");
        ArrayAdapter<String> spinnerAktifitas = new ArrayAdapter<String>(
                this.rootView.getContext(),
                android.R.layout.simple_spinner_item, aktifitas){
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
        spinnerAktifitas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputAktif.setAdapter(spinnerAktifitas);

        /*script untuk spinner aktifitas*/
        List<String> hamil = new ArrayList<>();
        hamil.add("Pilihan...");
        hamil.add("Trimester 1");
        hamil.add("Trimester 2");
        hamil.add("Trimester 3");
        hamil.add("Menyusui");
        ArrayAdapter<String> spinnerHamil = new ArrayAdapter<String>(
                this.rootView.getContext(),
                android.R.layout.simple_spinner_item, hamil){
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
        spinnerHamil.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputHamil.setAdapter(spinnerHamil);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_kalori, menu);
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
}
