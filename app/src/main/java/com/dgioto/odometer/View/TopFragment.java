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

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dgioto.odometer.MainActivity;
import com.dgioto.odometer.model.Odometer;
import com.dgioto.odometer.Service.NotificationService;
import com.dgioto.odometer.Service.OdometerService;
import com.dgioto.odometer.R;
import com.dgioto.odometer.model.Stopwatch;

public class TopFragment extends Fragment implements View.OnClickListener {

    //ODOMETER
    private Odometer odometer;
    private OdometerService odometerService;
    private final int PERMISSION_REQUEST_CODE = 698;

    //STOPWATCH
    private Stopwatch stopWatch;

    private final MainActivity mainActivity;
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
            odometerService = odometerBinder.getOdometer();
            odometer.bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            odometer.bound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ODOMETER
        odometer = new Odometer();
        odometerService = new OdometerService();

        //STOPWATCH
        stopWatch = new Stopwatch();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_top, container, false);

        distanceView = layout.findViewById(R.id.distance);

        timeView = layout.findViewById(R.id.time);
        stopWatch.runTimer(timeView);

        startButton = layout.findViewById(R.id.start);
        startButton.setOnClickListener(this);
        noteButton = layout.findViewById(R.id.note);
        noteButton.setOnClickListener(this);
        dischargeButton = layout.findViewById(R.id.discharge);
        dischargeButton.setOnClickListener(this);

        return layout;
    }

    private void onClickStart(){

        if (ContextCompat.checkSelfPermission(mainActivity, OdometerService.PERMISSION_STRING)
                != PackageManager.PERMISSION_GRANTED){

            addLocationDialog();

        } else {
            Intent intent = new Intent(mainActivity, OdometerService.class);
            mainActivity.bindService(intent, connection, Context.BIND_AUTO_CREATE);

            mainActivity.managerGPS();
            if (!mainActivity.statusOfGPS){
                AlertDialog statusOfGPSDialog = new AlertDialog.Builder(mainActivity).create();
                statusOfGPSDialog.setTitle(R.string.gps);
                LinearLayout view = (LinearLayout)
                        getLayoutInflater().inflate(R.layout.dialog_status_of_gps, null);
                statusOfGPSDialog.setView(view);
                statusOfGPSDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                statusOfGPSDialog.show();
            } else {
                noteButton.setVisibility(View.VISIBLE);
                dischargeButton.setVisibility(View.VISIBLE);
                startButton.setText(R.string.restart);

                //ODOMETER
                odometerService.resetDistance();
                odometer.bound = true;
                odometer.displayDistance(distanceView, odometerService);

                //STOPWATCH
                stopWatch.running = true;
                stopWatch.seconds = 0;

                startNotificationService();
            }

        }
    }

    private void addLocationDialog() {
        androidx.appcompat.app.AlertDialog locationDialog =
                new androidx.appcompat.app.AlertDialog.Builder(mainActivity).create();
        locationDialog.setTitle(R.string.location);
        locationDialog.setCancelable(false);
        LinearLayout view_location = (LinearLayout)
                getLayoutInflater().inflate(R.layout.dialog_location, null);
        locationDialog.setView(view_location);
        locationDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE,
                "Yes",
                (dialog, which) -> {
                    ActivityCompat.requestPermissions(mainActivity,
                            new String[]{OdometerService.PERMISSION_STRING},
                            PERMISSION_REQUEST_CODE);
                });
        locationDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE,
                "No",
                (dialog, which) -> dialog.dismiss());
        locationDialog.show();
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
        startButton.setText(R.string.start);

        //ODOMETER
        odometer.bound = false;
        odometerService.resetDistance();

        //STOPWATCH
        stopWatch.running = false;
        stopWatch.seconds = 0;
    }

    private void startNotificationService(){
        Intent intent = new Intent(mainActivity, NotificationService.class);
        intent.putExtra(NotificationService.EXTRA_MESSAGE,
                getResources().getString(R.string.notification));
        mainActivity.startService(intent);
    }

    //ODOMETER
    //when permission was requested at runtime, check the result
    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(mainActivity, OdometerService.class);
                odometerService.bindService(intent, connection, Context.BIND_AUTO_CREATE);
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
                        (NotificationManager) odometerService.getSystemService(Context.NOTIFICATION_SERVICE);
                int NOTIFICATION_ID = 423;
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (odometer.bound){
            odometerService.unbindService(connection);
            odometer.bound = false;
        }
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