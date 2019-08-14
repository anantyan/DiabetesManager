package id.co.medical.management.activity;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.medical.management.R;
import id.co.medical.management.api.AuthApi;
import id.co.medical.management.component.RecordsComponent;
import id.co.medical.management.component.ResponseComponent;
import id.co.medical.management.component.SharedPreferencesComponent;
import id.co.medical.management.fragment.HomeFragment;
import id.co.medical.management.fragment.KaloriFragment;
import id.co.medical.management.fragment.LaporanFragment;
import id.co.medical.management.fragment.MakananFragment;
import id.co.medical.management.fragment.OlahragaFragment;
import id.co.medical.management.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_nav_main) BottomNavigationView bottomNav;
    @BindView(R.id.toolbar) Toolbar toolbar;

    int getMenuChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);

        setSupportActionBar(toolbar);

        bottomNav.setOnNavigationItemSelectedListener(navBar);
        //bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true); // make checked Home Fragment
        bottomNav.getMenu().findItem(R.id.nav_home).setEnabled(false); // make checked Home Fragment

        loadFragment(new HomeFragment()); // default Home Fragment
    }

    //make not animation transition
    /*@Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0,0);
    }*/

    private BottomNavigationView.OnNavigationItemSelectedListener navBar = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectFrag;

            getMenuChecked = menuItem.getItemId();
            for (int i=0; i<bottomNav.getMenu().size(); i++){
                MenuItem menuT = bottomNav.getMenu().getItem(i);
                boolean isEnable = menuT.getItemId() != getMenuChecked;
                //boolean isChecked = menuT.getItemId() == getMenuChecked;
                menuT.setEnabled(isEnable);
                //menuT.setChecked(isChecked);
            }

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectFrag = new HomeFragment();
                    loadFragment(selectFrag);
                    toolbar.setTitle(R.string.app_name);
                    return true;
                case R.id.nav_makanan:
                    selectFrag = new MakananFragment();
                    loadFragment(selectFrag);
                    toolbar.setTitle("Makanan");
                    return true;
                case R.id.nav_olahraga:
                    selectFrag = new OlahragaFragment();
                    alertFragment(
                            selectFrag,
                            "Apakah anda hari sedang Olahraga atau sedang Cek gula darah?",
                            "Olahraga & Cek Gula");
                    return true;
                case R.id.nav_laporan:
                    selectFrag = new LaporanFragment();
                    loadFragment(selectFrag);
                    toolbar.setTitle("Laporan");
                    return true;
                case R.id.nav_create:
                    selectFrag = new KaloriFragment();
                    alertFragment(
                            selectFrag,
                            "Apakah anda ingin mengubah data?",
                            "Kalori");
                    toolbar.setTitle("Kalori");
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content, fragment);
        fragmentTransaction.commit();
    }

    private void alertFragment(Fragment fragment, String msg, String name){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Konfirmasi");
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        loadFragment(fragment);
                        toolbar.setTitle(name);
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
