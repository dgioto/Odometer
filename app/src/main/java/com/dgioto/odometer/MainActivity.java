package com.dgioto.odometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.dgioto.odometer.View.HistoryFragment;
import com.dgioto.odometer.View.TopFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter pagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager(), this);
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final MainActivity mA;

        public SectionsPagerAdapter(FragmentManager fm, MainActivity _mA) {
            super(fm);
            this.mA = _mA;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case  0:
                    return  new TopFragment(mA);
                case  1:
                    return new HistoryFragment(mA);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getResources().getText(R.string.distance_tab);
                case 1:
                    return getResources().getText(R.string.history_tab);
            }
            return null;
        }
    }
}