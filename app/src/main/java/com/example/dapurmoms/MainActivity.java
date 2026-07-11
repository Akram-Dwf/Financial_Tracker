package com.example.dapurmoms;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.dapurmoms.ui.beranda.BerandaFragment;
import com.example.dapurmoms.ui.pesanan.PesananFragment;
import com.example.dapurmoms.ui.belanja.BelanjaFragment;
import com.example.dapurmoms.ui.biaya.BiayaFragment;
import com.example.dapurmoms.ui.laporan.LaporanFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Schedule Trash Auto-cleanup
        androidx.work.PeriodicWorkRequest cleanupRequest =
                new androidx.work.PeriodicWorkRequest.Builder(com.example.dapurmoms.worker.TrashCleanupWorker.class, 1, java.util.concurrent.TimeUnit.DAYS)
                        .build();
        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "TrashCleanupWork",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                cleanupRequest
        );

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        
        // Prevent swiping from becoming too slow/laggy by retaining fragments
        viewPager.setOffscreenPageLimit(4);

        // Sync BottomNavigation with ViewPager2
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.berandaFragment) {
                if (viewPager.getCurrentItem() != 0) viewPager.setCurrentItem(0, false);
                return true;
            } else if (itemId == R.id.pesananFragment) {
                if (viewPager.getCurrentItem() != 1) viewPager.setCurrentItem(1, false);
                return true;
            } else if (itemId == R.id.belanjaFragment) {
                if (viewPager.getCurrentItem() != 2) viewPager.setCurrentItem(2, false);
                return true;
            } else if (itemId == R.id.biayaFragment) {
                if (viewPager.getCurrentItem() != 3) viewPager.setCurrentItem(3, false);
                return true;
            } else if (itemId == R.id.laporanFragment) {
                if (viewPager.getCurrentItem() != 4) viewPager.setCurrentItem(4, false);
                return true;
            }
            return false;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNav.setSelectedItemId(R.id.berandaFragment);
                        break;
                    case 1:
                        bottomNav.setSelectedItemId(R.id.pesananFragment);
                        break;
                    case 2:
                        bottomNav.setSelectedItemId(R.id.belanjaFragment);
                        break;
                    case 3:
                        bottomNav.setSelectedItemId(R.id.biayaFragment);
                        break;
                    case 4:
                        bottomNav.setSelectedItemId(R.id.laporanFragment);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_trash) {
            com.example.dapurmoms.ui.trash.TrashDialogFragment.newInstance()
                    .show(getSupportFragmentManager(), "TrashDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class MainPagerAdapter extends FragmentStateAdapter {

        public MainPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1:
                    return new PesananFragment();
                case 2:
                    return new BelanjaFragment();
                case 3:
                    return new BiayaFragment();
                case 4:
                    return new LaporanFragment();
                case 0:
                default:
                    return new BerandaFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }
}