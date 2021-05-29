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

import java.util.Random;

public class OdometerService extends Service {

    private final IBinder binder = new OdometerBinder();
    private final Random random = new Random();

    private LocationListener listener;

    private LocationManager locManager;
    //строка разрешения добавляется в виде константы
    public  static final  String PERMISSION_STRING = Manifest.permission.ACCESS_FINE_LOCATION;

    public OdometerService() {
    }

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

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
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
                locManager.requestLocationUpdates(provider, 100, 1, listener);
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public double getDistance(){
        return random.nextDouble();
    }
}