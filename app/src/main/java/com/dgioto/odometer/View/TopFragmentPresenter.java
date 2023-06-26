package com.dgioto.odometer.View;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dgioto.odometer.MainActivity;
import com.dgioto.odometer.R;
import com.dgioto.odometer.Service.NotificationService;
import com.dgioto.odometer.Service.OdometerService;

import java.util.Locale;

public class TopFragmentPresenter implements TopFragmentContract.Presenter {

    private final MainActivity mainActivity;
    private final TopFragmentContract.View topFragment;

    //ODOMETER
    public boolean bound = false;
    private OdometerService odometerService;
    private final int PERMISSION_REQUEST_CODE = 698;

    //STOPWATCH
    private int seconds = 0;
    private boolean running;

    public TopFragmentPresenter(TopFragmentContract.View topFragment, MainActivity mainActivity) {
        this.topFragment = topFragment;
        this.mainActivity = mainActivity;

        odometerService = new OdometerService();
    }

    //ODOMETER
    public final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder = (OdometerService.OdometerBinder) binder;
            odometerService = odometerBinder.getOdometer();
            bound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    //ODOMETER
    public void displayDistance(TextView distanceView, OdometerService odometerService){

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (bound && odometerService != null){
                    distance = odometerService.getDistance();
                }
                String distanceStr = String.format(Locale.getDefault(),
                        "%1$,.0f m", distance);
                distanceView.setText(distanceStr);
                handler.postDelayed(this, 1000);
            }
        });
    }

    //STOPWATCH
    public void runTimer(TextView timeView){

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
    public boolean getBound() {
        return bound;
    }

    @Override
    public void setBound(boolean bound) {
        this.bound = bound;
    }

    @Override
    public ServiceConnection getConnection() {
        return connection;
    }

    @Override
    public void onClickStart() {

        if (ContextCompat.checkSelfPermission(mainActivity, OdometerService.PERMISSION_STRING)
                != PackageManager.PERMISSION_GRANTED){

            addLocationDialog();

        } else {
            Intent intent = new Intent(mainActivity, OdometerService.class);
            mainActivity.bindService(intent, connection, Context.BIND_AUTO_CREATE);

            mainActivity.managerGPS();
            if (!mainActivity.statusOfGPS){
                Context context = topFragment.getContext();
                androidx.appcompat.app.AlertDialog statusOfGPSDialog =
                        new androidx.appcompat.app.AlertDialog.Builder(context).create();
                statusOfGPSDialog.setTitle(R.string.gps);
                LayoutInflater inflater = LayoutInflater.from(topFragment.getContext());
                LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_status_of_gps, null);
                statusOfGPSDialog.setView(view);
                statusOfGPSDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                statusOfGPSDialog.show();
            } else {
                topFragment.getNoteButton().setVisibility(View.VISIBLE);
                topFragment.getDischargeButton().setVisibility(View.VISIBLE);
                topFragment.getStartButton().setText(R.string.restart);

                //ODOMETER
                odometerService.resetDistance();
                bound = true;
                displayDistance(topFragment.getDistanceView(), odometerService);

                //STOPWATCH
                running = true;
                seconds = 0;

                startNotificationService();
            }
        }
    }

    private void startNotificationService(){
        Intent intent = new Intent(mainActivity, NotificationService.class);
        intent.putExtra(NotificationService.EXTRA_MESSAGE,
                mainActivity.getResources().getString(R.string.notification));
        mainActivity.startService(intent);
    }

    @Override
    public void onClickNote() {
        Intent intent = new Intent(mainActivity, EditActivity.class);
        intent.putExtra("distanceView", topFragment.getDistanceView().getText().toString());
        intent.putExtra("timeView", topFragment.getTimeView().getText().toString());
        mainActivity.startActivity(intent);
    }

    @Override
    public void onClickDischarge() {
        topFragment.getNoteButton().setVisibility(View.GONE);
        topFragment.getDischargeButton().setVisibility(View.GONE);
        topFragment.getStartButton().setText(R.string.start);

        //ODOMETER
        bound = false;
        odometerService.resetDistance();

        //STOPWATCH
        running = false;
        seconds = 0;
    }

    @Override
    public void addLocationDialog() {
        Context context = topFragment.getContext();
        androidx.appcompat.app.AlertDialog locationDialog =
                new androidx.appcompat.app.AlertDialog.Builder(context).create();
        locationDialog.setTitle(R.string.location);
        locationDialog.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(topFragment.getContext());
        LinearLayout view_location = (LinearLayout) inflater.inflate(R.layout.dialog_location, null);
        locationDialog.setView(view_location);
        locationDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE,
                "Yes",
                (dialog, which) -> ActivityCompat.requestPermissions(mainActivity,
                        new String[]{OdometerService.PERMISSION_STRING},
                        PERMISSION_REQUEST_CODE));
        locationDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE,
                "No",
                (dialog, which) -> dialog.dismiss());
        locationDialog.show();
    }

    @Override
    public void onRequestPermissionsResultToPresenter(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(mainActivity, OdometerService.class);
                odometerService.bindService(intent, connection, Context.BIND_AUTO_CREATE);
            } else {
                Notification.Builder builder = new Notification.Builder(mainActivity)
                        .setSmallIcon(android.R.drawable.ic_menu_compass)
                        .setContentTitle(mainActivity.getResources().getString(R.string.app_name))
                        .setContentText(mainActivity.getResources().getString(R.string.permission_denied))
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                Intent actionIntent = new Intent(mainActivity, MainActivity.class);
                PendingIntent actionPendingIntent = PendingIntent.getActivity(mainActivity,
                        0,
                        actionIntent,
                        PendingIntent.FLAG_MUTABLE);
                builder.setContentIntent(actionPendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) odometerService.getSystemService(Context.NOTIFICATION_SERVICE);
                int NOTIFICATION_ID = 423;
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }
    }
}
