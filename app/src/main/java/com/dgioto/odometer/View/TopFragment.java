package com.dgioto.odometer.View;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dgioto.odometer.MainActivity;
import com.dgioto.odometer.OdometerService;
import com.dgioto.odometer.R;

import java.util.Locale;

public class TopFragment extends Fragment implements View.OnClickListener {

    //STOPWATCH
    //количество прошедших секунд
    private int seconds = 0;
    //флаг работы секундомера
    private boolean running;

    //ODOMETER
    //сохранение ссылки на службу
    private OdometerService odometer;
    //признак связывания с активностью
    private boolean bound = false;
    private final int PERMISSION_REQUEST_CODE = 698;
    private final int NOTIFICATION_ID = 423;

    MainActivity mainActivity;
    View layout;

    private TextView distanceView;
    private TextView timeView;

    public  TopFragment(MainActivity _mainActivity){
        this.mainActivity = _mainActivity;
    }

    //ODOMETER
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ODOMETER
        //устраняет Баг: вылет программы при нажатии на кнопку СТАРТ или РЕСТАРТ после первого запуска
        odometer = new OdometerService();

        if (ContextCompat.checkSelfPermission(mainActivity,
                OdometerService.PERMISSION_STRING)
                != PackageManager.PERMISSION_GRANTED){
            //Запросить разрешение ACCESS_FINE_LOCATION, если оно не было дано ранее
            ActivityCompat.requestPermissions(mainActivity,
                    new String[]{OdometerService.PERMISSION_STRING},
                    PERMISSION_REQUEST_CODE);
        } else {
            //Интент, отправленный OdometerService
            Intent intent = new Intent(mainActivity, OdometerService.class);
            //connection является объектом ServiceConnection
            //Метод bindService() использует интент и соединение со службой для связывания активности со службой
            mainActivity.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        //STOPWATCH
        //сохраняем переменные в объект Bundle
        if (savedInstanceState != null){
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_top, container, false);

        distanceView = layout.findViewById(R.id.distance);

        //представление макета передается при вызове метода runTime()
        runTimer(layout);

        //связываем слушатель с каждой из кнопок
        Button startButton = layout.findViewById(R.id.start);
        startButton.setOnClickListener(this);
        Button stopButton = layout.findViewById(R.id.stop);
        stopButton.setOnClickListener(this);
        Button resetButton = layout.findViewById(R.id.reset);
        resetButton.setOnClickListener(this);
        Button exitButton = layout.findViewById(R.id.exit);
        exitButton.setOnClickListener(this);

        return layout;
    }

    private void onClickStart(){
        //ODOMETER
        odometer.resetDistance();
        bound = true;
        displayDistance();

        //STOPWATCH
        running = true;
    }

    private void onClickStop(){
        Intent intent = new Intent(mainActivity, EditActivity.class);
        intent.putExtra("distanceView", distanceView.getText().toString());
        intent.putExtra("timeView", timeView.getText().toString());
        startActivity(intent);
    }

    private void onClickDischarge(){
        //ODOMETER
        bound = false;
        odometer.resetDistance();

        //STOPWATCH
        running = false;
        seconds = 0;
    }

    private void onClickExit(){
        AlertDialog.Builder ald = new AlertDialog.Builder(mainActivity);
        ald.setTitle(R.string.exit)
            .setMessage(R.string.do_you_exit)
            .setPositiveButton(R.string.yes, (dialog, i) -> {
                mainActivity.finish();
                System.exit(1);
            })
            .setNegativeButton(R.string.not, (dialog, i) -> dialog.cancel())
            .show();
        ald.create();
    }

    //ODOMETER
    //если у пользователя запрашивалось разрешение во время выполнения, проверить результат
    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions
            , @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(mainActivity, OdometerService.class);
                //выполнить связывание со службой если пользователь предоставил разрешение
                odometer.bindService(intent, connection, Context.BIND_AUTO_CREATE);
            } else {
                //создание построителя уведомления
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mainActivity)
                        .setSmallIcon(android.R.drawable.ic_menu_compass)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(getResources().getString(R.string.permission_denied))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                //создание действия
                Intent actionIntent = new Intent(mainActivity, MainActivity.class);
                PendingIntent actionPendingIntent = PendingIntent.getActivity(mainActivity, 0,
                        actionIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(actionPendingIntent);

                //выдача уведомления
                NotificationManager notificationManager =
                        (NotificationManager) odometer.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }

    //STOPWATCH
    //сохранить состояние секундомера, если он готовится к уничтожению
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("running", running);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bound){
            //Использует объект ServiceConnection для отмены связывания со службой
            odometer.unbindService(connection);
            //при разрыве связи со службой присваивается false
            bound = false;
        }
    }

    //ODOMETER
    //будет обновляться каждую секунду, а надпись в MainActivity будет обновляться полученным значением
    private void displayDistance(){
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
                        "%1$,.0f m", distance);
                distanceView.setText(distanceStr);
                //значение TextView обновляется каждую секунду
                handler.postDelayed(this, 1000);
            }
        });
    }

    //STOPWATCH
    //обновление показаний таймера
    private void runTimer(View view){
        timeView = view.findViewById(R.id.time);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start:
                onClickStart();
                break;
            case R.id.stop:
                onClickStop();
                break;
            case R.id.reset:
                onClickDischarge();
                break;
            case R.id.exit:
                onClickExit();
                break;
        }
    }
}