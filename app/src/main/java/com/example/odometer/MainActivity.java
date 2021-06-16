package com.example.odometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.example.odometer.fragments.HistoryFragment;
import com.example.odometer.fragments.TopFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Связывание SectionsPagerAdapter c ViewPager
        //используются фрагменты из библиотеки поддержки, поэтому адаптеру необходимо передать
        //ссылку на диспетчерфрагментов поддержки
        SectionsPagerAdapter pagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager pager = findViewById(R.id.pager);
        //Написанный ранее код FragmentPagerAdapter присоединяется к ViewPager
        pager.setAdapter(pagerAdapter);

        //Связывание ViewPager с TabLayout
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
    }







    //адаптер страничного компонента фрагментов
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //необходим для указания какой фрагмент далжен выводиться на каждой странице
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case  0:
                    return  new TopFragment();
                case  1:
                    return new HistoryFragment();
            }
            return null;
        }

        //необходим для определения количества страниц в ViewPager
        @Override
        public int getCount() {
            return 2;
        }

        //добавляем текстна вкладки
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                //добавляем строковые ресурсы для вкладок
                case 0:
                    return getResources().getText(R.string.distance_tab);
                case 1:
                    return getResources().getText(R.string.history_tab);
            }
            return null;
        }
    }
}