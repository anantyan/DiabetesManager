package id.co.medical.management.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.medical.management.R;
import id.co.medical.management.fragment.HomeFragment;
import id.co.medical.management.fragment.KaloriFragment;
import id.co.medical.management.fragment.LaporanFragment;
import id.co.medical.management.fragment.MakananFragment;
import id.co.medical.management.fragment.OlahragaFragment;

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

            // set unckecked all without Home Fragment
            getMenuChecked = menuItem.getItemId();
            for (int i=0; i<bottomNav.getMenu().size(); i++){
                MenuItem menuT = bottomNav.getMenu().getItem(i);
                boolean isChecked = menuT.getItemId() == menuItem.getItemId();
                menuT.setChecked(isChecked);
            }

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    selectFrag = new HomeFragment();
                    loadFragment(selectFrag);
                    toolbar.setTitle(R.string.app_name);
                    break;
                case R.id.nav_makanan:
                    selectFrag = new MakananFragment();
                    loadFragment(selectFrag);
                    toolbar.setTitle("Makanan");
                    break;
                case R.id.nav_olahraga:
                    selectFrag = new OlahragaFragment();
                    loadFragment(selectFrag);
                    toolbar.setTitle("Olahraga & Cek Gula");
                    break;
                case R.id.nav_laporan:
                    selectFrag = new LaporanFragment();
                    loadFragment(selectFrag);
                    toolbar.setTitle("Laporan");
                    break;
                case R.id.nav_create:
                    selectFrag = new KaloriFragment();
                    loadFragment(selectFrag);
                    toolbar.setTitle("Kalori");
                    break;
            }
            return true;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, fragment);
        transaction.commit();
    }
}
