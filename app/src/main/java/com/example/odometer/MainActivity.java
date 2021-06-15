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

    //сохранение ссылки на службу
    private OdometerService odometer;
    //признак связывания с активностью
    private boolean bound = false;
    private final int PERMISSION_REQUEST_CODE = 698;
    private final int NOTIFICATION_ID = 423;


    //Создаем объект ServiceConnection
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder =
                    (OdometerService.OdometerBinder) binder;
            //Реализация IBinder используется для получения ссылки на службу
            odometer = odometerBinder.getOdometer();
            //Активность связывается со службой, переменной bound присваивается true
            bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            /* переменной bound присваивается значение false, так как активность MainActivity
            уже не связана с OdometerService */
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this,
                OdometerService.PERMISSION_STRING)
                != PackageManager.PERMISSION_GRANTED){
            //Запросить разрешение ACCESS_FINE_LOCATION, если оно не было дано ранее
            ActivityCompat.requestPermissions(this,
                    new String[]{OdometerService.PERMISSION_STRING},
                    PERMISSION_REQUEST_CODE);
        } else {
            //Интент, отправленный OdometerService
            Intent intent = new Intent(this, OdometerService.class);
            //connection является объектом ServiceConnection
            //Метод bindService() использует интент и соединение со службой для связывания активности со службой
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

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







    //если у пользователя запрашивалось разрешение во время выполнения, проверить результат
    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions
            , @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, OdometerService.class);
                //выполнить связывание со службой если пользователь предоставил разрешение
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
            } else {
                //создание построителя уведомления
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_menu_compass)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(getResources().getString(R.string.permission_denied))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                //создание действия
                Intent actionIntent = new Intent(this, MainActivity.class);
                PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 0,
                        actionIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(actionPendingIntent);

                //выдача уведомления
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound){
            //ИСпользет объект ServiceConnection для отмены связывания со службой
            unbindService(connection);
            //при разрыве связи со службой присваивается false
            bound = false;
        }
    }

    //будет обновляться каждую секунду, а надпись в MainActivity будет обновляться полученным значением
    private void displayDistance(){
        final TextView distanceView = findViewById(R.id.distance);
        //создаем объект Handler
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (bound && odometer != null){
                    /* Если имеется ссылка на OdometerService и связывание со службой было выполнено
                    вызвать getDistance() */
                    distance = odometer.getDistance();
                }
                String distanceStr = String.format(Locale.getDefault(),
                        "%1$,.1f метров", distance);
                distanceView.setText(distanceStr);
                //значение TextView обновляется каждую секунду
                handler.postDelayed(this, 1000);
            }
        });
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