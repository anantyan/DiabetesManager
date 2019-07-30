package id.co.medical.management.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.RestrictionEntry;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.List;
import java.util.Objects;

import id.co.medical.management.R;
import id.co.medical.management.activity.MainActivity;
import id.co.medical.management.api.AuthApi;
import id.co.medical.management.api.KaloriApi;
import id.co.medical.management.component.RecordsComponent;
import id.co.medical.management.component.ResponseComponent;
import id.co.medical.management.component.SharedPreferencesComponent;
import id.co.medical.management.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

    TextView txtKalori, txtTargetKalori, txtId, txtName, txtNomorHP;
    View rootView, rootLayout;
    SwipeRefreshLayout swipeRefreshLayout;

    private List<RecordsComponent> records;
    private int PERMISSION_ALLOW = 1;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*initialisation*/
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rootLayout = (CoordinatorLayout) rootView.findViewById(R.id._home);
        txtId = (TextView) rootView.findViewById(R.id._txt_id);
        txtKalori = (TextView) rootView.findViewById(R.id._txt_kalori);
        txtTargetKalori = (TextView) rootView.findViewById(R.id._txt_target_kalori);
        txtName = (TextView) rootView.findViewById(R.id._txt_name);
        txtNomorHP = (TextView) rootView.findViewById(R.id._txt_nomor_hp);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id._swipe_refresh);

        /*cek permission device support a camera or not*/
        if (ContextCompat.checkSelfPermission(this.rootView.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.rootView.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this.getActivity(), new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_ALLOW);
            } else {
                ActivityCompat.requestPermissions(this.getActivity(), new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, PERMISSION_ALLOW);
            }
        }

        txtId.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //LoadData
                getDataId();
                getKalori();

                //StopAnimate with Delay
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        return rootView;
    }

    /*cek permission device support a camera or not*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_ALLOW) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Anda berhasil izin", Toast.LENGTH_SHORT).show();
            } else {
                getActivity().finish();
            }
        }
    }

    private void getDataId(){
        Retrofit retrofit = RetrofitUtil.getClient();
        AuthApi authApi = retrofit.create(AuthApi.class);
        Call<ResponseComponent> call = authApi.getData();
        call.enqueue(new Callback<ResponseComponent>() {

            @Override
            public void onResponse(Call<ResponseComponent> call, Response<ResponseComponent> response) {
                records = response.body() != null ? response.body().getRecords() : null;
                if (response.isSuccessful()) {
                    if(records != null){
                        RecordsComponent recordsComponent = records.get(0);
                        String id = recordsComponent.getId();
                        String kalori = recordsComponent.getKalori();
                        String name = recordsComponent.getName();
                        String nomorHP = recordsComponent.getNomorHP();
                        String targetKalori = recordsComponent.getTargetKalori();

                        /*save in shared preference*/
                        new SharedPreferencesComponent(getActivity()).setDataIn(id);
                        txtId.setText(id);
                        txtKalori.setText(kalori);
                        txtName.setText(name);
                        txtNomorHP.setText(nomorHP);
                        txtTargetKalori.setText(targetKalori);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseComponent> call, Throwable t) {
                String id = new SharedPreferencesComponent(getActivity()).getDataId();
                if(id == null){
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
            }
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
                records = response.body().getRecords();
                if (response.isSuccessful()) {

                    if(records != null){
                        RecordsComponent recordsComponent = records.get(0);
                        String id = recordsComponent.getId();
                        String kalori = recordsComponent.getKalori();
                        String name = recordsComponent.getName();
                        String nomorHP = recordsComponent.getNomorHP();
                        String targetKalori = recordsComponent.getTargetKalori();

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
                                txtId.setText(id);
                                txtKalori.setText(kalori);
                                txtName.setText(name);
                                txtNomorHP.setText(nomorHP);
                                txtTargetKalori.setText(targetKalori);
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
                                txtId.setText(id);
                                txtKalori.setText(kalori);
                                txtName.setText(name);
                                txtNomorHP.setText(nomorHP);
                                txtTargetKalori.setText(targetKalori);
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
                                txtId.setText(id);
                                txtKalori.setText(kalori);
                                txtName.setText(name);
                                txtNomorHP.setText(nomorHP);
                                txtTargetKalori.setText(targetKalori);
                                break;
                            }
                            default:
                                txtId.setText(id);
                                txtKalori.setText(kalori);
                                txtName.setText(name);
                                txtNomorHP.setText(nomorHP);
                                txtTargetKalori.setText(targetKalori);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseComponent> call, Throwable t) {
                String id = new SharedPreferencesComponent(getActivity()).getDataId();
                if(id != null){
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

    @Override
    public void onResume() {
        getDataId();
        getKalori();
        super.onResume();
    }
}
