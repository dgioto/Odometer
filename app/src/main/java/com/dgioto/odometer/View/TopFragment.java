package com.dgioto.odometer.View;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgioto.odometer.MainActivity;
import com.dgioto.odometer.OdometerService;
import com.dgioto.odometer.R;

import java.util.Locale;

public class TopFragment extends Fragment implements View.OnClickListener {

    //STOPWATCH
    private int seconds = 0;
    private boolean running;

    //ODOMETER
    private OdometerService odometer;
    private boolean bound = false;
    private final int PERMISSION_REQUEST_CODE = 698;
    private final int NOTIFICATION_ID = 423;

    private MainActivity mainActivity;
    private View layout;
    private TextView distanceView, timeView;
    private Button startButton, noteButton, dischargeButton;

    public  TopFragment(MainActivity _mainActivity){
        this.mainActivity = _mainActivity;
    }

    //ODOMETER
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) binder;
            odometer = odometerBinder.getOdometer();
            bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ODOMETER
        odometer = new OdometerService();
        if (ContextCompat.checkSelfPermission(mainActivity, OdometerService.PERMISSION_STRING)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(mainActivity, new String[]{OdometerService.PERMISSION_STRING},
                    PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(mainActivity, OdometerService.class);
            mainActivity.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

        //STOPWATCH
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

        runTimer(layout);

        startButton = layout.findViewById(R.id.start);
        startButton.setOnClickListener(this);
        noteButton = layout.findViewById(R.id.note);
        noteButton.setOnClickListener(this);
        dischargeButton = layout.findViewById(R.id.discharge);
        dischargeButton.setOnClickListener(this);

        return layout;
    }

    private void onClickStart(){
            noteButton.setVisibility(View.VISIBLE);
            dischargeButton.setVisibility(View.VISIBLE);

            //ODOMETER
            odometer.resetDistance();
            bound = true;
            displayDistance();

            //STOPWATCH
            running = true;
            seconds = 0;
    }

    private void onClickNote(){
        Intent intent = new Intent(mainActivity, EditActivity.class);
        intent.putExtra("distanceView", distanceView.getText().toString());
        intent.putExtra("timeView", timeView.getText().toString());
        startActivity(intent);
    }

    private void onClickDischarge(){
        noteButton.setVisibility(View.GONE);
        dischargeButton.setVisibility(View.GONE);

        //ODOMETER
        bound = false;
        odometer.resetDistance();

        //STOPWATCH
        running = false;
        seconds = 0;
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
                odometer.bindService(intent, connection, Context.BIND_AUTO_CREATE);
            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(mainActivity)
                        .setSmallIcon(android.R.drawable.ic_menu_compass)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(getResources().getString(R.string.permission_denied))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                Intent actionIntent = new Intent(mainActivity, MainActivity.class);
                PendingIntent actionPendingIntent = PendingIntent.getActivity(mainActivity, 0,
                        actionIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(actionPendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) odometer.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }

    //STOPWATCH
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
            odometer.unbindService(connection);
            bound = false;
        }
    }

    //ODOMETER
    private void displayDistance(){
        //создаем объект Handler
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (bound && odometer != null){
                    distance = odometer.getDistance();
                }
                String distanceStr = String.format(Locale.getDefault(),
                        "%1$,.0f m", distance);
                distanceView.setText(distanceStr);
                handler.postDelayed(this, 1000);
            }
        });
    }

    //STOPWATCH
    private void runTimer(View view){
        timeView = view.findViewById(R.id.time);

        final Handler handler = new Handler();
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
            case R.id.note:
                onClickNote();
                break;
            case R.id.discharge:
                onClickDischarge();
                break;
        }
    }
}