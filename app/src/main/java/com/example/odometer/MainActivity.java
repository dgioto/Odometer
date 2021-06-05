package com.example.odometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //сохранение ссылки на службу
    private OdometerService odometer;
    //признак связывания сс активностью
    private boolean bound = false;
    private final int PERMISSION_REQUEST_CODE = 698;
    private final int NOTIFICATION_ID = 423;

    //STOPWATCH
    //количество прошедших секунд
    private int seconds = 0;
    //флаг работы секундомера
    private boolean running;
    //переменная для проверки, работал ли секундомер перед вызовом метода onStop()
    private  boolean wasRunning;

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

        displayDistance();

        //STOPWATCH
        //сохраняем переменные в объект Bundle
        if (savedInstanceState != null){
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }
        //обнавляем показания таймера
        runTimer();
    }

    public void onClickStart(View view){
        running = true;
    }

    public void onClickStop(View view){
        running = false;
    }

    public void onClickReset(View view){
        running = false;
        seconds = 0;
    }

    public void onClickExit(View view){

    }

    //STOPWATCH
    //сохранить состояние секундомера, если он готовится к уничтожению
    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
        savedInstanceState.putBoolean("wasRunning", wasRunning);
    }

    //если у пользователя запрашивалось разрешение во время выполнения, проверить результат
    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions
            , @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
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
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                        "%1$,.0f метров", distance);
                distanceView.setText(distanceStr);
                //значение TextView обновляется каждую секунду
                handler.postDelayed(this, 1000);
            }
        });
    }

    /*
    STOPWATCH
    обновление показаний таймера
     */
    private void runTimer(){
        final TextView timeView = (TextView) findViewById(R.id.time);
        //объект для выполнения кода в другом программном потоке
        final Handler handler = new Handler();
        //запускаем отдельный поток
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);
                if(running) seconds++;
                //повторное выполнение кода с отсрочкой в 1 секунду
                handler.postDelayed(this, 1000);
            }
        });
    }
}