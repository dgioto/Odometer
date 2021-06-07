package com.example.odometer;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;


public class OdometerService extends Service {

    private final IBinder binder = new OdometerBinder();
    private LocationListener listener;
    private LocationManager locManager;
    //расстояние и последнее местонахождение пользователя хранится в статических переменных,
    //что бы их значения сохранялись при уничтожении службы
    private static double distanceInMeters;
    private static Location lastLocation = null;
    //строка разрешения добавляется в виде константы
    public  static final  String PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;

    public class OdometerBinder extends Binder{
        OdometerService getOdometer(){
            return OdometerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        listener = new LocationListener() {
            @Override
            //Параметр Location описывает текущее местоположение
            public void onLocationChanged(@NonNull Location location) {
                //задаем исходное значение местонахождения пользователя
                if (lastLocation == null){
                    lastLocation = location;
                }
                distanceInMeters += location.distanceTo(lastLocation);
                //обновляем пройденное расстояние и последнее местонахождение пользователя
                lastLocation = location;
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {    }

            @Override
            public void onProviderEnabled(@NonNull String provider) {    }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {   }
        };

        //получаем объект LocationManager
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //проверить наличее разрешения
        if (ContextCompat.checkSelfPermission(this, PERMISSION_STRING)
                == PackageManager.PERMISSION_GRANTED) {
            //получить самый точный провайдер
            String provider = locManager.getBestProvider(new Criteria(), true);
            if (provider != null){
                //запросить обновления от провайдера данных местонахождения
                locManager.requestLocationUpdates(provider, 1000, 1, listener);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locManager != null && listener != null){
            if (ContextCompat.checkSelfPermission(this, PERMISSION_STRING)
                    == PackageManager.PERMISSION_GRANTED){
                //прекратить получение обновлений, если имеется разрешение на их удаление
                locManager.removeUpdates(listener);
            }
            locManager = null;
            listener = null;
        }
    }

    public double getDistance(){
        return this.distanceInMeters;
    }

    public void resetDistance(){
        this.distanceInMeters = 0;
    }
}