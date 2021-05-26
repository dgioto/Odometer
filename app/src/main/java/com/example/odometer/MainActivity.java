package com.example.odometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class MainActivity extends AppCompatActivity {

    //переменные для сохранения ссылки на службу и признака связывания с активностью
    private OdometerService odometer;
    private boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

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

        }
    };
}