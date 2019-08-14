package id.co.medical.management.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.co.medical.management.R;
import id.co.medical.management.activity.MainActivity;
import id.co.medical.management.api.KaloriApi;
import id.co.medical.management.component.RecordsComponent;
import id.co.medical.management.component.ResponseComponent;
import id.co.medical.management.component.SharedPreferencesComponent;
import id.co.medical.management.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OlahragaFragment extends Fragment {

    Button btnSave;
    ProgressDialog progressDialog;
    View rootView, rootLayout;
    EditText inputJenisOlahraga, inputHasilGulaDarah;
    Spinner inputDurasiOlahraga, inputGulaDarah;

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
        rootView = inflater.inflate(R.layout.fragment_olahraga, container, false);
        rootLayout = (CoordinatorLayout) rootView.findViewById(R.id._olahraga);
        inputJenisOlahraga = (EditText) rootView.findViewById(R.id._input_jenis_olahraga);
        inputHasilGulaDarah = (EditText) rootView.findViewById(R.id._input_hasil_cek_gula_darah);
        inputDurasiOlahraga = (Spinner) rootView.findViewById(R.id._input_durasi_olahraga);
        inputGulaDarah = (Spinner) rootView.findViewById(R.id._input_cek_gula_darah);
        btnSave = (Button) rootView.findViewById(R.id._btn_save);

        alertKonfirmasi();
        spinnerDurasiOlahraga();
        spinnerGulaDarah();
        btnSave();

        return rootView;
    }

    private void alertKonfirmasi() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
        alertDialogBuilder.setTitle("Konfirmasi");
        alertDialogBuilder
                .setMessage("Apakah anda hari ini Olahraga atau Sedang Cek Gula Darah?")
                .setCancelable(false)
                .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(rootView.getContext(), MainActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        getActivity().overridePendingTransition(0,0);
                        getActivity().finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void btnSave() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] valDurasiOlahraga = new String[]{"", "10 Menit", "15 Menit", "30 Menit", "1 Jam", "Lebih dari 1 Jam"};
                String[] valGulaDarah = new String[]{"", "Gula Darah Puasa", "Gula Darah Sewaktu", "Gula Darah 2 Jam setelah makan"};

                String id = new SharedPreferencesComponent(rootView.getContext()).getDataId();
                String jenisOlahraga = inputJenisOlahraga.getText().toString().trim();
                String durasiOlahraga = valDurasiOlahraga[inputDurasiOlahraga.getSelectedItemPosition()];
                String gulaDarah = valGulaDarah[inputGulaDarah.getSelectedItemPosition()];
                String hasilGulaDarah = inputHasilGulaDarah.getText().toString().trim();

                if(jenisOlahraga.equals("")){
                    Toast.makeText(rootView.getContext(),
                            "Jenis Olahraga harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputJenisOlahraga.setError("Jenis Olahraga Darah harus diisi!");
                    inputJenisOlahraga.requestFocus();
                }else if(durasiOlahraga.equals("")){
                    Toast.makeText(rootView.getContext(),
                            "Durasi Olahraga harus diisi!",
                            Toast.LENGTH_LONG).show();
                    inputDurasiOlahraga.requestFocus();
                }else {
                    progressDialog = new ProgressDialog(rootView.getContext());
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Tunggu...");
                    progressDialog.show();

                    Retrofit retrofit = RetrofitUtil.getClient();
                    KaloriApi kaloriApi = retrofit.create(KaloriApi.class);
                    Call<ResponseComponent> call = kaloriApi.updateOlahraga(id, jenisOlahraga, durasiOlahraga, gulaDarah, hasilGulaDarah);
                    call.enqueue(new Callback<ResponseComponent>() {
                        @Override
                        public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                            assert response.body() != null;
                            String error = response.body().getError();
                            String status = response.body().getStatus();
                            progressDialog.dismiss();
                            if (error.equals("1")) {
                                Toast.makeText(rootView.getContext(), status, Toast.LENGTH_SHORT).show();
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
                    /*Toast.makeText(rootView.getContext(), id+", "+
                            jenisOlahraga+", "+
                            durasiOlahraga+", "+
                            gulaDarah+", "+
                            hasilGulaDarah, Toast.LENGTH_SHORT).show();*/
                }
            }
        });
    }

    /*private void inputCekGulaDarah() {
        inputGulaDarah.setEnabled(true);
        inputHasilGulaDarah.setEnabled(true);
        inputCekGulaDarah.setChecked(true);

        inputCekGulaDarah.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    inputGulaDarah.setEnabled(false);
                    inputHasilGulaDarah.setEnabled(false);
                    btnSave();

                    *//*set reset*//*
                    inputGulaDarah.setSelection(0);
                    inputHasilGulaDarah.setText("");
                }else{
                    inputGulaDarah.setEnabled(true);
                    inputHasilGulaDarah.setEnabled(true);
                    btnSaveCekGulaDarah();
                }
            }
        });
    }*/

    /*private void inputOlahraga() {
        inputJenisOlahraga.setEnabled(true);
        inputDurasiOlahraga.setEnabled(true);
        inputOlahraga.setChecked(true);

        inputOlahraga.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    inputJenisOlahraga.setEnabled(false);
                    inputDurasiOlahraga.setEnabled(false);
                    btnSave();

                    set reset
                    inputJenisOlahraga.setText("");
                    inputDurasiOlahraga.setSelection(0);
                }else{
                    inputJenisOlahraga.setEnabled(true);
                    inputDurasiOlahraga.setEnabled(true);
                    btnSaveOlahraga();
                }
            }
        });
    }*/

    private void spinnerGulaDarah() {
        final List<String> listHasilGulaDarah = new ArrayList<>();
        listHasilGulaDarah.add("Pilihan...");
        listHasilGulaDarah.add("Gula Darah Puasa");
        listHasilGulaDarah.add("Gula Darah Sewaktu");
        listHasilGulaDarah.add("Gula Darah 2 Jam setelah makan");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.rootView.getContext(), android.R.layout.simple_spinner_item, listHasilGulaDarah){
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
        inputGulaDarah.setAdapter(adapter);
    }

    private void spinnerDurasiOlahraga() {
        final List<String> listDurasiOlahraga = new ArrayList<>();
        listDurasiOlahraga.add("Pilihan...");
        listDurasiOlahraga.add("10 Menit");
        listDurasiOlahraga.add("15 Menit");
        listDurasiOlahraga.add("30 Menit");
        listDurasiOlahraga.add("1 Jam");
        listDurasiOlahraga.add("Lebih dari 1 Jam");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.rootView.getContext(), android.R.layout.simple_spinner_item, listDurasiOlahraga){
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
        inputDurasiOlahraga.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_olahraga, menu);
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
