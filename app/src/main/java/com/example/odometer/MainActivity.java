package com.example.odometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //переменные для сохранения ссылки на службу и признака связывания с активностью
    private OdometerService odometer;
    private boolean bound = false;

    //Создаем объект ServiceConnection
    private ServiceConnection connection = new ServiceConnection() {
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
            //переменно  bound присваивается значение false, так как активность MainActivity
            // уже не связана с OdometerService
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayDistance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Интент, отправленный OdometerService
        Intent intent = new Intent(this, OdometerService.class);
        //connection является объектом ServiceConnection
        //Метод bindService() использует интент и соединение со службой для связывания активности со службой
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                    //Если имеется ссылка на OdometerService и связывание со службой было выполненоб
                    // вызвать getDistance()
                    distance = odometer.getDistance();
                }
                String distanceStr = String.format(Locale.getDefault(),
                        "%1$, .2f meters", distance);
                distanceView.setText(distanceStr);
                //значение TextView обновляется каждую секунду
                handler.postDelayed(this, 5000);
            }
        });
    }
}