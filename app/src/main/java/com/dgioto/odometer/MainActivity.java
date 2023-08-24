package com.dgioto.odometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgioto.odometer.View.HistoryFragment;
import com.dgioto.odometer.View.TopFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter pagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager(), getLifecycle());
        ViewPager2 pager = findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, pager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getResources().getText(R.string.distance_tab));
                    break;
                case 1:
                    tab.setText(getResources().getText(R.string.history_tab));
                    break;
            }
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         if (item.getItemId() == R.id.about) {
             AlertDialog aboutDialog = new AlertDialog.Builder(MainActivity.this).create();
             aboutDialog.setTitle(R.string.about);
             LinearLayout view = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_about, null);
             aboutDialog.setView(view);
             TextView version = view.findViewById(R.id.version);
             //app version
             String appVersionName = BuildConfig.VERSION_NAME;
             version.setText(appVersionName);
             aboutDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                     (dialog, which) -> dialog.dismiss());
             aboutDialog.show();
             return true;
        }
        if (item.getItemId() == R.id.privacy_policy) {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, R.string.runtracker_privacypolicy);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class SectionsPagerAdapter extends FragmentStateAdapter {

        public SectionsPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @Override
        @NonNull
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new TopFragment();
                case 1:
                    return new HistoryFragment();
            }
            throw new IllegalArgumentException("Invalid position: " + position);
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}