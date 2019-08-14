package id.co.medical.management.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.medical.management.R;
import id.co.medical.management.component.CekDataComponent;
import id.co.medical.management.component.SharedPreferencesComponent;
import id.co.medical.management.utils.RetrofitUtil;

public class LaporanCetakActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id._input_pilih_data) Spinner pilihData;
    @BindView(R.id._btn_next) Button btnNext;

    private int PERMISSION_ALLOW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_cetak);
        ButterKnife.bind(LaporanCetakActivity.this);

        String id = new SharedPreferencesComponent(this).getDataId(); // get data id
        new CekDataComponent(this, findViewById(R.id._laporan_cetak_activity)).CekDataId(id); // cek apakah akun terblokir atau tidak

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.title_activity_laporan_cetak);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spinnerPilihData();
        btnNext();

        /*cek permission device support a camera or not*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            permissionGranted();
        }
    }

    /*cek permission device support a camera or not*/
    private void permissionGranted() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_ALLOW);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, PERMISSION_ALLOW);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_ALLOW) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                onBackPressed();
            }
        }
    }

    private void spinnerPilihData() {
        final List<String> listPilihData = new ArrayList<>();
        listPilihData.add("Pilihan...");
        listPilihData.add("Menampilkan 1 Hari");
        listPilihData.add("Menampilkan 7 Hari");
        listPilihData.add("Menampilkan 30 Hari");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LaporanCetakActivity.this, android.R.layout.simple_spinner_item, listPilihData){
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
        pilihData.setAdapter(adapter);
    }

    private void btnNext() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = new SharedPreferencesComponent(LaporanCetakActivity.this).getDataId();
                String[] stringData = new String[]{
                        "",
                        "?p%3DPrint%26id%3D"+id+"%26x%3DoneDay",
                        "?p%3DPrint%26id%3D"+id+"%26x%3DweekDays",
                        "?p%3DPrint%26id%3D"+id+"%26x%3DmonthDays"
                };
                String data = stringData[pilihData.getSelectedItemPosition()];

                if(data.equals("")){
                    Toast.makeText(LaporanCetakActivity.this,
                            "Pilih berdasarkan harus diisi!",
                            Toast.LENGTH_LONG).show();
                    pilihData.requestFocus();
                }else{
                    Uri uri = Uri.parse("https://docs.google.com/viewerng/viewer?url="+RetrofitUtil.BASE_URL+data);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    /*Bundle bundle = new Bundle();
                    bundle.putString("keyData", RetrofitUtil.BASE_URL+data);
                    intent.putExtras(bundle);*/
                    startActivity(intent);
                    finish();

                    /*Toast.makeText(LaporanCetakActivity.this, RetrofitUtil.BASE_URL+data, Toast.LENGTH_SHORT).show();*/
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fragment_olahraga, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_about){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LaporanCetakActivity.this);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
