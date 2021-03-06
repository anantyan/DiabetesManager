package id.co.medical.management.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import id.co.medical.management.R;
import id.co.medical.management.activity.ProfileActivity;
import id.co.medical.management.api.AuthApi;
import id.co.medical.management.api.KaloriApi;
import id.co.medical.management.component.CekDataComponent;
import id.co.medical.management.component.RecordsComponent;
import id.co.medical.management.component.ResponseComponent;
import id.co.medical.management.component.SharedPreferencesComponent;
import id.co.medical.management.utils.LoadImageUtil;
import id.co.medical.management.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static id.co.medical.management.activity.ProfileActivity.EXTRA_NAME_PROFILE;
import static id.co.medical.management.activity.ProfileActivity.EXTRA_NOMORHP_PROFILE;
import static id.co.medical.management.activity.ProfileActivity.EXTRA_PHOTO_PROFILE;

public class HomeFragment extends Fragment {

    TextView txtKalori, txtTargetKalori, txtName, txtNomorHP;
    ImageView photoData;
    View rootView, rootLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    Button btnObat;
    ProgressDialog progressDialog;
    RelativeLayout profileFragmentHome;

    private List<RecordsComponent> records;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*initialisation*/
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rootLayout = (CoordinatorLayout) rootView.findViewById(R.id._home);
        txtKalori = (TextView) rootView.findViewById(R.id._txt_kalori);
        txtTargetKalori = (TextView) rootView.findViewById(R.id._txt_target_kalori);
        txtName = (TextView) rootView.findViewById(R.id._txt_name);
        txtNomorHP = (TextView) rootView.findViewById(R.id._txt_nomor_hp);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id._swipe_refresh);
        btnObat = (Button) rootView.findViewById(R.id.btn_obat);
        profileFragmentHome = (RelativeLayout) rootView.findViewById(R.id.profile_fragment_home);
        photoData = (ImageView) rootView.findViewById(R.id._photo_data);

        String id = new SharedPreferencesComponent(this.getActivity()).getDataId(); // get data id
        new CekDataComponent(this.getActivity(), rootLayout).CekDataId(id); // cek apakah akun terblokir atau tidak

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getKalori(); // untuk load data

            new CekDataComponent(this.getActivity(), rootLayout).CekDataId(id); // cek apakah akun terblokir atau tidak

            swipeRefreshLayout.setRefreshing(false); // stopAnimate with delay
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        minumObat();
//        profile();

        return rootView;
    }

    private void profile(){
        profileFragmentHome.setOnClickListener(v -> {

            String id = new SharedPreferencesComponent(this.getActivity()).getDataId();

            Retrofit retrofit = RetrofitUtil.getClient();
            AuthApi authApi = retrofit.create(AuthApi.class);
            Call<ResponseComponent> call = authApi.profile(id);
            call.enqueue(new Callback<ResponseComponent>() {
                @Override
                public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                    String error = response.body() != null ? response.body().getError() : null;
                    String status = response.body() != null ? response.body().getStatus() : null;
                    records = response.body() != null ? response.body().getRecords() : null;
                    if(response.isSuccessful()) {
                        if(error != null){
                            switch(error){
                                case "0":
                                    Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();
                                    break;
                                case "1":
                                    if(records != null){
                                        RecordsComponent recordsComponent = records.get(0);
                                        String name = recordsComponent.getNama();
                                        String nomorHP = recordsComponent.getNomor_hp();
                                        String photoProfile = null;
                                        Intent i = new Intent(rootView.getContext(), ProfileActivity.class);
                                        Bundle b = new Bundle();
                                        b.putString(EXTRA_NAME_PROFILE, name);
                                        b.putString(EXTRA_NOMORHP_PROFILE, nomorHP);
                                        b.putString(EXTRA_PHOTO_PROFILE, photoProfile);
                                        i.putExtras(b);
                                        startActivity(i);
                                    }
                                    break;
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseComponent> call, Throwable t) {
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
        });
    }

    private void minumObat() {
        btnObat.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
            alertDialogBuilder.setTitle("Form minum obat");
            alertDialogBuilder
                    .setMessage("Apakah anda hari ini sudah minum obat?")
                    .setCancelable(false)
                    .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String dataId = new SharedPreferencesComponent(getActivity()).getDataId();

                            progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Tunggu...");
                            progressDialog.show();

                            Retrofit retrofit = RetrofitUtil.getClient();
                            KaloriApi kaloriApi = retrofit.create(KaloriApi.class);
                            Call<ResponseComponent> call = kaloriApi.insertObat(dataId);
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
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }

    private void getKalori() {
        String dataId = new SharedPreferencesComponent(this.getActivity()).getDataId();

        Retrofit retrofit = RetrofitUtil.getClient();
        KaloriApi kaloriApi = retrofit.create(KaloriApi.class);
        Call<ResponseComponent> call = kaloriApi.selectKalori(dataId);
        call.enqueue(new Callback<ResponseComponent>() {
            @Override
            public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                assert response.body() != null;
                String error = response.body().getError();
                String status = response.body().getStatus();
                records = response.body() != null ? response.body().getRecords() : null;
                if (response.isSuccessful()) {
                    if(records != null) {
                        RecordsComponent recordsComponent = records.get(0);
                        String kalori = recordsComponent.getKalori();
                        String name = recordsComponent.getNama();
                        String nomorHP = recordsComponent.getNomor_hp();
                        String targetKalori = recordsComponent.getTarget_kalori();
                        String photoProfile = null;
                        switch (error) {
                            case "1": {
                                /*Custom title alert dialog*/
                                TextView titleDialog = new TextView(rootView.getContext());
                                titleDialog.setText("Peringatan!");
                                titleDialog.setPadding(32, 30, 32, 30);
                                titleDialog.setTextSize(20F);
                                titleDialog.setBackgroundColor(Color.GREEN);
                                titleDialog.setTextColor(Color.BLACK);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
                                alertDialogBuilder.setCustomTitle(titleDialog);
                                alertDialogBuilder
                                        .setMessage(status)
                                        .setCancelable(false)
                                        .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                                txtKalori.setText(kalori);
                                txtName.setText(name);
                                txtNomorHP.setText(nomorHP);
                                txtTargetKalori.setText(targetKalori);
                                LoadImageUtil l = new LoadImageUtil(rootView.getContext());
                                l.setGlideWithAccent(photoProfile, photoData);
                                break;
                            }
                            case "2": {
                                /*Custom title alert dialog*/
                                TextView titleDialog = new TextView(rootView.getContext());
                                titleDialog.setText("Peringatan!");
                                titleDialog.setPadding(32, 30, 32, 30);
                                titleDialog.setTextSize(20F);
                                titleDialog.setBackgroundColor(Color.RED);
                                titleDialog.setTextColor(Color.WHITE);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
                                alertDialogBuilder.setCustomTitle(titleDialog);
                                alertDialogBuilder
                                        .setMessage(status)
                                        .setCancelable(false)
                                        .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                                txtKalori.setText(kalori);
                                txtName.setText(name);
                                txtNomorHP.setText(nomorHP);
                                txtTargetKalori.setText(targetKalori);
                                LoadImageUtil l = new LoadImageUtil(rootView.getContext());
                                l.setGlideWithAccent(photoProfile, photoData);
                                break;
                            }
                            case "3": {
                                /*Custom title alert dialog*/
                                TextView titleDialog = new TextView(rootView.getContext());
                                titleDialog.setText("Peringatan!");
                                titleDialog.setPadding(32, 30, 32, 30);
                                titleDialog.setTextSize(20F);
                                titleDialog.setBackgroundColor(Color.YELLOW);
                                titleDialog.setTextColor(Color.BLACK);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());
                                alertDialogBuilder.setCustomTitle(titleDialog);
                                alertDialogBuilder
                                        .setMessage(status)
                                        .setCancelable(false)
                                        .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                                txtKalori.setText(kalori);
                                txtName.setText(name);
                                txtNomorHP.setText(nomorHP);
                                txtTargetKalori.setText(targetKalori);
                                LoadImageUtil l = new LoadImageUtil(rootView.getContext());
                                l.setGlideWithAccent(photoProfile, photoData);
                                break;
                            }
                            case "4":
                                txtKalori.setText(kalori);
                                txtName.setText(name);
                                txtNomorHP.setText(nomorHP);
                                txtTargetKalori.setText(targetKalori);
                                LoadImageUtil l = new LoadImageUtil(rootView.getContext());
                                l.setGlideWithAccent(photoProfile, photoData);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseComponent> call, Throwable t) {
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_about){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.rootView.getContext());
            alertDialogBuilder
                    .setTitle("Tentang Aplikasi")
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
        //getDataId();
        getKalori();
        super.onResume();
    }
}
