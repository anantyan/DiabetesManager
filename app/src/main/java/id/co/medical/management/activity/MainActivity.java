package id.co.medical.management.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.medical.management.R;
import id.co.medical.management.fragment.HomeFragment;
import id.co.medical.management.fragment.KaloriFragment;
import id.co.medical.management.fragment.LaporanFragment;
import id.co.medical.management.fragment.MakananFragment;
import id.co.medical.management.fragment.OlahragaFragment;

import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;

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
        bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true); // make checked Home Fragment
        //bottomNav.getMenu().findItem(R.id.nav_home).setEnabled(false); // make checked Home Fragment

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
                //boolean isEnable = menuT.getItemId() != getMenuChecked;
                boolean isChecked = menuT.getItemId() == getMenuChecked;
                //menuT.setEnabled(isEnable);
                menuT.setChecked(isChecked);
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
                    loadFragment(selectFrag);
                    toolbar.setTitle("Olahraga & Cek Gula");
                    return true;
                case R.id.nav_laporan:
                    selectFrag = new LaporanFragment();
                    loadFragment(selectFrag);
                    toolbar.setTitle("Laporan");
                    return true;
                case R.id.nav_create:
                    selectFrag = new KaloriFragment();
                    loadFragment(selectFrag);
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
}
